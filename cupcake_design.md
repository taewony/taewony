# Cupcake App Design

## 1. App Structure and Functionality

The Cupcake app is a simple app that allows users to order cupcakes. It consists of the following screens:

*   **Start Screen:** The initial screen that welcomes the user and has a button to start the order.
*   **Flavor Screen:** Allows the user to select the flavor of the cupcake.
*   **Pickup Screen:** Allows the user to select the pickup date.
*   **Summary Screen:** Displays a summary of the order and allows the user to place another order.

The app is built using Jetpack Compose and follows a Model-View-ViewModel (MVVM) architecture.

*   **View:** The UI is built with composable functions (`StartScreen`, `FlavorScreen`, etc.).
*   **ViewModel:** The `OrderViewModel` holds the state of the order (quantity, flavor, date) and handles the business logic.
*   **Model:** The data is simple and is managed directly in the `OrderViewModel`.

## 2. What is Nav3?

Nav3 is a declarative, state-based navigation library for Jetpack Compose. Unlike traditional navigation systems that rely on fragments and navigation graphs, Nav3 uses a simple list to manage the back stack.

The core components of Nav3 are:

*   **`NavDisplay`:** A composable that displays the current screen based on the state of the back stack.
*   **`NavEntry`:** A wrapper around a composable that represents a screen in the back stack.
*   **Back Stack:** A simple `MutableList` that holds the navigation state.

## 3. How Nav3 is applied in the Cupcake App

The Cupcake app uses Nav3 to manage the navigation between the different screens.

*   **`CupcakeApp.kt`:** This is the main entry point for the app's UI. It creates and manages the back stack, which is a `mutableStateListOf<Any>`.
*   **`CupcakeScreen.kt`:** This sealed interface defines the different screens in the app as objects (`Start`, `Flavor`, `Pickup`, `Summary`).
*   **`NavDisplay`:** The `CupcakeApp` composable uses a `NavDisplay` to display the current screen. The `entryProvider` lambda maps each `CupcakeScreen` object to a `NavEntry` that contains the corresponding screen composable.
*   **Navigation:** Navigation is handled by simply adding or removing items from the back stack. For example, to navigate to the `Flavor` screen, we use `backStack += CupcakeScreen.Flavor`. To go back, we use `backStack.removeLastOrNull()`.

This approach to navigation is simple, flexible, and easy to understand, as it's all managed in one place and uses simple state management.
