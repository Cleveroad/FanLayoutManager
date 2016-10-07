#FanLayoutManager [![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/sindresorhus/awesome) <img src="https://www.cleveroad.com/public/comercial/label-android.svg" height="20"> <a href="https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts"><img src="https://www.cleveroad.com/public/comercial/label-cleveroad.svg" height="20"></a>
![Header image](/images/header.jpg)

## Welcome to Fan Layout Manager for Android by Cleveroad

Fan Layout Manager is a new library created at Cleveroad. Our ideas don’t come from nowhere, everything we invent results from the difficulties we overcome. This exactly what happened — we were fighting uniformity! Since traditional view of the simple horizontal list isn’t impressive anymore, we decided to suggest our vision adding some colours and a completely new motion path. 

![Demo image](/images/demo_.gif)

#####Check out the animation <strong><a target="_blank" href="https://youtu.be/sJhBKyvF7i4?list=PLi-FH7__aeiydOwY_1q5I8P2EUSseqUCj">Fan Layout Manager for Android on YouTube</a></strong> in HD quality.

Cleveroad gladly shares its creation with everyone who wants to add some visual spice to their Android apps. Take Fan Layout Manager and create!

Using Fan Layout Manager you can implement the horizontal list, the items of which move like fan blades (in a circular way of a radius to your choice). To give a slightly chaotical effect to the motion, it’s possible to set an angle for the list items. So, every implementation of the library can be unique.

[![Awesome](/images/logo-footer.png)](https://www.cleveroad.com/?utm_source=github&utm_medium=label&utm_campaign=contacts)

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

## Support
If you have any questions regarding the use of this tutorial, please contact us for support
at info@cleveroad.com (email subject: «FanLayoutManager for Android. Support request.»)
<br>or
<br>Use our contacts:
<br><a href="https://www.cleveroad.com/?utm_source=github&utm_medium=link&utm_campaign=contacts">Cleveroad.com</a>
<br><a href="https://www.facebook.com/cleveroadinc">Facebook account</a>
<br><a href="https://twitter.com/CleveroadInc">Twitter account</a>
<br><a href="https://plus.google.com/+CleveroadInc/">Google+ account</a>

## License


        The MIT License (MIT)

        Copyright (c) 2015-2016 Cleveroad

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
