package dev.ganidroid.tagview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var tagLayout: TagsLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tagLayout = findViewById(R.id.tagLayout)

        val tags = ArrayList<TagsLayout.Tag>()
        tags.add(TagsLayout.Tag(1, "Inception", false))
        tags.add(TagsLayout.Tag(2, "Parasite", false))
        tags.add(TagsLayout.Tag(3, "Joker", true))
        tags.add(TagsLayout.Tag(4, "Shoplifters", false))
        tags.add(TagsLayout.Tag(5, "Wonder Women", true))
        tags.add(TagsLayout.Tag(6, "The invisible man", true))
        tags.add(TagsLayout.Tag(7, "Birds of pray", false))
        tags.add(TagsLayout.Tag(8, "Hello", false))
        tags.add(TagsLayout.Tag(9, "Oreo", false))
        tags.add(TagsLayout.Tag(10, "Ganidroid", false))

        tagLayout?.setTags(tags)
        tagLayout?.setTagClickListener(TagsLayout.TagClickListener { it ->
            Toast.makeText(applicationContext, "Tag: ${it.tag}", Toast.LENGTH_LONG).show()

        })
    }
}