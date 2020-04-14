# Project archive, because it does not have any sense. Use default way for work with recycler view and diff utils.

# RxFastRecyclerView
The library provides a base adapter for recycler view and ObservableTransform for transform data for the adapter. The library update data into recycler view faster when default android way.

[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=schoolhelper/RxFastRecyclerView)](https://dependabot.com)

[![](https://jitpack.io/v/schoolhelper/RxFastRecyclerView.svg)](https://jitpack.io/#schoolhelper/RxFastRecyclerView)

## Status
### Dev branch
[![Build Status](https://travis-ci.org/schoolhelper/androidgraphextension.svg?branch=dev)](https://travis-ci.org/schoolhelper/androidgraphextension)
[![codecov](https://codecov.io/gh/schoolhelper/RxFastRecyclerView/branch/dev/graph/badge.svg)](https://codecov.io/gh/schoolhelper/RxFastRecyclerView)

### Master brach
[![Build Status](https://travis-ci.org/schoolhelper/androidgraphextension.svg?branch=master)](https://travis-ci.org/schoolhelper/androidgraphextension)
[![codecov](https://codecov.io/gh/schoolhelper/RxFastRecyclerView/branch/master/graph/badge.svg)](https://codecov.io/gh/schoolhelper/RxFastRecyclerView)

## We are faster
We don't recreate view holder for update data.

## How to use
### ViewHolder
Extend your view holder from `FastUpdateViewHolder<YourEntity>`
You need to implement the following methods:
`initEntity`
`setupListeners`

### Adapter
Extend your recycler view adapter from `FastAdapter<YourEntity, YourViewHodler>`
You need to implement the following method:
`onCreateViewHolder`

### Differ
You need to implement Differ for your entity. Extend from `ListDiffer<YourEntity>`
You need to implement the following methods:
`areItemTheSame` - returns true if the two items represent the same object or false if they are different.
`areContentTheSame`(optional) - returns true if the contents of the items are the same or false if they are different.
Don't use this library for each of your recyclers — the library good for a case when your recycler show data from some Observable.

### Transform data
```kotlin
yourObservable
  .compose(storyContentDiffer.transformToDiff())
  .subscribe(adapter::updateContent)
```

## Dependencies
We use the following external library:
  1. RxJava
  2. AndroidX recyclerview

You need to have the following dependencies into your project:
```groovy
androidx.recyclerview:recyclerview:1.x.y'
io.reactivex.rxjava2:rxjava:2.x.y
```
x, y - any available version of the libraries.

Don't worry. We don't add any external dependencies to your project. The library uses rxJava and androidX which already added to your project.
