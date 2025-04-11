# TaigaMobile
This is an **unofficial** compose multiplatform app for the agile project management system [taiga.io](https://www.taiga.io/). It was built with [Jetpack Compose](https://developer.android.com/jetpack/compose), featuring Material You with dynamic colors.

## Features
* View:
  * Projects
  * Epics
  * User stories
  * Tasks
  * Issues
  * Sprints
  * Profiles
  * Wiki
  * Working on / Watching (aka Dashboard)
* Create, edit and delete:
  * Epics
  * User stories
  * Tasks
  * Issues
  * Sprints
  * Wiki pages
* Leave and delete comments
* Kanban (for sprint and for user stories)
* Filters for user stories, epics, issues

## Stack
* Kotlin
* Compose Multiplatform
* Clean Architecture
* MVVM
* Coroutines
* ... and other cool things

## Design
Probably sucks. I'm not very good at designing UI, but I did my best.

## Structure
This is a standard Compose Multiplatform project: 
* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…