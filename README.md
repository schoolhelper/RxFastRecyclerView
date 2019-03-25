# RxFastRecyclerView
The library provide base adapter for recycler view and ObservableTransform for transform data for adapter. The library update data into recycler view more faster when default andoid way.

[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=schoolhelper/RxFastRecyclerView)](https://dependabot.com)

## Status
### Dev branch
[![Build Status](https://travis-ci.org/schoolhelper/androidgraphextension.svg?branch=dev)](https://travis-ci.org/schoolhelper/androidgraphextension)

### Master brach
[![Build Status](https://travis-ci.org/schoolhelper/androidgraphextension.svg?branch=master)](https://travis-ci.org/schoolhelper/androidgraphextension)

## We are faster
We don't recreate view holder for update data.

## How to use
### ViewHolder
Exetend your view holder from `FastUpdateViewHolder<YourEntity>`
You need to implement the following methods:
`initEntity`
`setupListeners`

### Adapter
Exetend your recycler view adapter from `FastAdapter<YourEntity, YourViewHodler>`
You need to implement the following method:
`onCreateViewHolder`

### Differ
Implement differ for your entity. Extend from `ListDiffer<YourEntity>`
You need to implemnt the following methods:
`areItemTheSame` - return true if the two items represent the same object or false if they are different.
`areContentTheSame`(optional) - return true if the contents of the items are the same or false if they are different.
Don't use this library for each your recycler. The library good for case when your recycler show data from some Observable.

### Transform data
```kotlin
yourObservable
  .compose(storyContentDiffer.transformToDiff())
  .subscribe(adapter::updateContent)
```
