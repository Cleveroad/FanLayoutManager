# FanLayoutManager #
[![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="20"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="20"></a>
![Header image](/images/header.jpg)

![Demo image](/images/demo.gif)

### Installation ###
by Gradle:
```groovy
    compile 'com.cleveroad:fan-layout-manager:1.0.1'
```
### Setup and usage ###
Use default FanLayoutManager in code:
```JAVA
fanLayoutManager = new FanLayoutManager(getContext());
recyclerView.setLayoutManager(fanLayoutManager);
```

You can **select/deselect** item using:
```JAVA
fanLayoutManager.switchItem(recyclerView, itemPosition); // select/deselect
fanLayoutManager.deselectItem();                         // just deselect
```

**Get selected item position**:
```JAVA
fanLayoutManager.getSelectedItemPosition(); // return selected item position
fanLayoutManager.isItemSelected();          // true if item was selected
```

To customize ***FanLayoutManager*** we provide settings:
```JAVA
FanLayoutManagerSettings fanLayoutManagerSettings = FanLayoutManagerSettings
                .newBuilder(getContext())
                .withFanRadius(true)
                .withAngleItemBounce(5)
                .withViewWidthDp(120)
                .withViewHeightDp(160)               
                .build();

fanLayoutManager = new FanLayoutManager(getContext(), fanLayoutManagerSettings);
recyclerView.setLayoutManager(fanLayoutManager);
```
**.withFanRadius(boolean isFanRadiusEnable)**    - you can enable displaying items in fan style.</p>
**.withAngleItemBounce(float angleItemBounce)**  - added rendom bounce angle to items from [0.. angleItemBounce).</p>
**.withViewWidthDp(float viewWidthDp)**          - custom item width. default 120dp.</p>
**.withViewHeightDp(float viewHeightDp)**         - custom item height. default 160dp.</p>

You can *remove bounce angle* effect for selected item:
```JAVA
public void straightenSelectedItem(Animator.AnimatorListener listener);
```

You can collapse views using:
```JAVA
public void collapseViews();
```

