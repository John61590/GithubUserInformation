# GitHub User Information App

An Android application that allows users to see a list of GitHub user profiles.
Also, an interview app for BitFlyer Japan 2025 written by John Bohne.

## Features

- **User List View**: Browse GitHub users with profile information
- **User Detail View**: View detailed profile information including followers, following, and public repositories
- **Swipe-to-Refresh**: Swipe down to refresh the user list
- **Image Loading**: Efficient avatar loading with Coil
- **Material Design UI**: Clean, modern interface following Material Design guidelines

## Architecture

This app follows a clean architecture pattern:

- **MVVM (Model-View-ViewModel)**: Separation of UI and business logic
- **StateFlow**: Modern reactive state management (Superior to legacy LiveData)
- **Dependency Injection**: Hilt for compile-time safe dependency injection
- **Repository Pattern**: Single source of truth for data operations
- **ViewBinding**: Type-safe view binding for UI components

## Tech Stack

- **Language**: Kotlin 2.0.21
- **UI**: Android Views (XML layouts) with ViewBinding
- **Architecture Components**: ViewModel, StateFlow, Lifecycle
- **Networking**: Retrofit 2.9.0 + Moshi
- **Dependency Injection**: Hilt 2.57.2
- **Image Loading**: Coil 2.7.0
- **Build System**: Gradle 8.13.2

## How to Build

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17 or later
- Android SDK 36

### Building
```bash
# Clone the repository
git clone <repository-url>
cd GithubUserInformation

# Build the project
./gradlew assembleDebug

# Run on emulator or connected device
./gradlew installDebug
```

### Running Tests
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run with coverage (if configured)
./gradlew testDebugUnitTest
```

## API Used

This app uses the GitHub REST API:
- **List Users**: `GET /users` - Lists all users
- **Get User**: `GET /users/{username}` - Gets detailed user information

Documentation: [GitHub REST API - Users](https://docs.github.com/en/rest/users/users)

## Testing

The project includes comprehensive unit tests with **33 tests** covering:

- Repository layer (data operations)
- ViewModel layer (state management and business logic)
- Data models (validation and equality)

### Test Coverage
- Repository: 6 tests
- MainViewModel: 6 tests
- UserDetailViewModel: 5 tests
- Data Models: 10 tests
- Others: 6 tests

All tests use:
- Mocking: Mockito-Kotlin
- Coroutines: Kotlin Coroutines Test
- Lifecycle: AndroidX Core Testing

## AI Tools Used

As per the assignment requirements, AI tools were used in the development of this project:

### ChatGPT / Claude AI (via Firebender)

**Use Case**: Unit Test Development and Code Refactoring

**Issues Resolved**:
1. Fixed 4 failing unit tests due to mock setup configuration
   - Problem: Mock tests using `any()` matcher caused failures
   - Solution: Used exact parameters instead of `any()`
   - Reason: Streamlined test debugging while maintaining understanding of test behavior

2. Refactored ViewModels from LiveData to StateFlow
   - Problem: Legacy LiveData API being used
   - Solution: Migrated to modern StateFlow with proper lifecycle-aware collection
   - Reason: StateFlow is the modern standard for Kotlin coroutines, provides better integration with Flow operators, thread safety by default, and eliminates Android dependencies from ViewModel layer

3. Fixed build compatibility issues with Kotlin dependency versions
   - Problem: Kotlin metadata version mismatch between dependencies
   - Solution: Aligned all dependencies to use Kotlin 2.0.21 compatible versions
   - Reason: Ensuring build stability and proper dependency management for production-level code

**Reason for Using AI Tools**:
AI tools were used to accelerate development while maintaining code quality. 
They helped identify and fix issues quickly, allowing focus on architectural decisions and user experience rather then debugging configuration problems. 

## Future Improvements (Idea for Enhancement)

If the opportunity arises to enhance this application further, here are potential improvements:

### Core Features
- **Search Functionality**: Add ability to search users by login using GitHub Search API (`GET /search/users`)
- **Pagination**: Implement infinite scroll pagination using GitHub's `since` parameter for better performance
- **Offline Support**: Add Room database caching to allow offline viewing of previously loaded users in case of no Internet

### UX Enhancements
- **Pull-to-Refresh Detail**: Add refresh capability to detail screen
- **Accessibility**: Improve screen reader support with proper content descriptions
- **Translations**: Add Japanese translations to some strings

### Technical Improvements
- **Compose Migration**: Migrate UI to Jetpack Compose for modern declarative UI
- **Crash Reporting**: Add crash analytics integration (e.g., Firebase Crashlytics)

## Project Structure

```
app/
├── src/main/java/com/example/githubuserinfo/
│   ├── data/
│   │   ├── GithubRepository.kt          # Data repository
│   │   ├── model/
│   │   │   ├── GithubUserDetail.kt      # User detail data class
│   │   │   └── GithubUserSummary.kt     # User summary data class
│   │   └── remote/
│   │       ├── GithubApiService.kt     # Retrofit API service
│   │       └── NetworkModule.kt         # Dependency injection
│   ├── ui/
│   │   ├── detail/
│   │   │   ├── UserDetailActivity.kt
│   │   │   └── UserDetailViewModel.kt
│   │   └── main/
│   │       ├── MainActivity.kt
│   │       ├── MainViewModel.kt
│   │       └── UserListAdapter.kt
│   └── GithubApplication.kt
└── src/test/                            # Unit tests
```

## License

This project was created for interview assessment purposes.

---

## Screenshots

1. User list with pull-to-refresh
-Light Theme
<img width="1344" height="2992" alt="GitHubUserInformation - List (Light Theme)" src="https://github.com/user-attachments/assets/5a2f9de7-71ba-4a97-8fd4-d28bb069ca43" />
-Dark Theme
<img width="1344" height="2992" alt="GitHubUserInformation - List (Dark Theme)" src="https://github.com/user-attachments/assets/aca895e7-6e05-491b-87b0-9037b55be75f" />
2. User detail view
<img width="1344" height="2992" alt="GitHub User Information - List Detail" src="https://github.com/user-attachments/assets/74107e01-0d2f-4b17-84f5-437c3cdc4e94" />
3. Empty State - No Internet (List Screen)
<img width="1344" height="2992" alt="GitHubUserInformation - List Error State" src="https://github.com/user-attachments/assets/e4556e1d-55e0-4025-bc77-a0b70f21b1af" />



