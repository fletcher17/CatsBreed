# CatsBreed

This is an app to view different breeds of cats. It was written in Kotlin/Jetpack Compose. it's
paginated with
searchable breed list, a favorite screen with an average lifespan summary (the lower bound) ad a
breed detail
screen, which is also offline capable with a Clean Architecture, uit tested viewmodels and usecases.

# Architecture

Clean Architecture in three layers, organised as packages within a single app module

```
domain/        Breed model, BreedRepository interface, use cases
data/          Retrofit DTOs, Room entities/DAO, BreedRepositoryImpl, mappers
presentation/  ViewModels (MVVM), Compose screens, navigation, theme
di/            Koin modules wiring it all together

```

**My Reason**: the domain layer has zero dependency on `data` internals or Android
framework classes, so `BreedRepository` is a pure interface the ViewModels depend on —
swapping Retrofit for GraphQL, or Room for a different cache, would only touch `data/`.
My Use cases are intentionally thin (mostly one line delegations); the exception is
`CalculateAverageLifespanUseCase`, which is a pure function isolated specifically so the
favourites-summary math can be unit tested without touching Flow or coroutines at all.

**Why single-module, not multi-module:** I know multi-module is a bonus point based, not
a requirement. So for an app this size, splitting into `:core-domain` / `:core-data` /
`:feature-*` Gradle modules mostly adds build-graph and Gradle-config overhead without a
real payoff, there's no team-scaling or independent build time problem to solve yet. The
*package* boundaries already enforce the same separation of concerns and are trivial to
promote into real Gradle modules later if the app grows (however, domain has no Android dependency
today, so it could become a pure-Kotlin module with no source changes if we want to scale up).

### Presentation pattern

MVVM with unidirectional data flow: each screen has one `StateFlow<UiState>` that's a
single source of truth, built with `combine()` over the repository's reactive flows plus
local UI only state (search query, loading/error flags). The list and detail screens use a
sealed/flag based `UiState` covering Loading, Success, Empty, Error explicitly, rather
than exposing raw exceptions or nullable breeds to Compose. The UI layer only ever pattern
matches on state.

### Concurrency & offline strategy

- **Room is the single source of truth for the UI.** The `breeds` table doubles as both the
  browsed catalogue cache and the favourites store (`isFavourite` + `sortIndex` columns are
  independent). BreedList, favourites, and detail screens all observe Room via `Flow`, so a
  favourite toggle updates every screen reactively with no manual refresh callback wiring.
- **Network calls only write through the cache**, they never feed Compose directly. This
  means: the list works offline once a page has been loaded before; toggling a favourite
  works fully offline (it's a local DB write); and a failed page-load or detail-refresh
  never clears already-cached data — the repository's `runCatching`/`recoverCatching` chain
  only *adds* an error message to the UI state, it never nulls out existing content.
  Search falls back to filtering the local cache by name when the network call fails.
- **Pagination is hand scrolled**, The ViewModel tracks a page counter and calls a suspend
  `loadBreedsPage(page, pageSize)`; the API's own `page`/`limit`
  params map directly onto that. I considered Paging3 for the "proper" library answer, but
  RemoteMediator's invalidation/refresh contract is real complexity to get right, and for a
  single unbounded list without jump to position or complex key-based paging, the trade-off
  wasn't worth it here, this keeps the caching logic in one place I can unit test directly
  instead of trusting a mediator's lifecycle.
- Search is debounced (350ms) and de-duplicated with `distinctUntilChanged` before hitting
  the network, to avoid firing a request per keystroke.


### Dependency Injection — Koin over Hilt

Koin was chosen deliberately, I have always used Dagger-Hilt for DI, so i opted to use Koin this time, also for it technical advantages
such as it less complexity to implement because it's a runtime DI graph with a small, explicit `module { }`,
no annotation processing/KAPT step, and faster incremental builds, a
reasonable trade for an app this size, where Koin's lack of compile-time graph validation
is a minor risk. Scoping: network,database,repository singletons live in `single { }`
(the app doesn't need per-screen network/db instances); use cases are `factory { }`
(cheap, stateless, no reason to keep them alive); ViewModels use Koin's `viewModel { }`
DSL so they're scoped to their `ViewModelStoreOwner` as normal, with `BreedDetailViewModel`
taking a runtime parameter (`breedId`) via `parametersOf`.

### Error/loading/empty states

Every screen's `UiState` distinguishes: initial loading, incremental loading (pagination
spinner at list end, distinct from a full screen spinner), search-in-flight, empty (no
results vs. no favourites, with different copy), and error (with a retry action). The list
screen keeps already loaded breeds on screen if a *subsequent* page fails, rather than
replacing the whole list with an error screen, only the very first load failing with
nothing cached shows the full screen error state.

## Security

- API key is build-time injected via `BuildConfig`, sourced from a gitignored
  `local.properties` / CI env var — never a literal in source, never in the repo.
- `x-api-key` is attached once, in a single OkHttp interceptor, rather than per call,
  one place to audit, one place to rotatte.
- `network_security_config.xml` disables cleartext traffic entirely (HTTPS only).
- The verbose OkHttp logging interceptor (which logs headers, including the API key) is
  wired to `HttpLoggingInterceptor.Level.NONE` in release builds (if this was to have 
  a debug and release build type) and `BODY` only in debug.

## Testing

- `CalculateAverageLifespanUseCaseTest` / `BreedModelTest`; The lifespan parsing and
  averaging math the favourites summary depends on, including malformed input fallback.
- `BreedListViewModelTest`, `FavouritesViewModelTest`, `BreedDetailViewModelTest` State
  transitions (loading,success,error,empty), favourite toggling, search clearing
  against a `FakeBreedRepository` rather than a mocked one, so tests assert on realistic
  Flow-driven behaviour instead of verifying mock call counts.
- `BreedRepositoryImplTest` — the two trickiest behaviours in the data layer: a favourite
  flag survives a subsequent catalogue re-fetch, and search degrades to filtering the local
  cache when the network is down. Uses an in-memory fake `BreedDao` to keep this a fast, plain JVM test.
- A small instrumented smoke/E2E test (`app/src/androidTest`) checks the three screens are
  reachable through the real DI graph and bottom navigation.
