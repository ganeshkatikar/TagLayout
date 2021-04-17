# TagLayout
Customize taglayout for tags with alignment center, left and right property.


## Add view in your layout

```android

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    tools:context=".MainActivity">


    <dev.ganidroid.tagview.TagsLayout
        android:id="@+id/tagLayout"
        app:tl_gravity="left"
        app:tl_bg_color_parent="#ebeb34"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</LinearLayout>
```

## License
[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)