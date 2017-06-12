package net.vrgsoft.rxurlparser

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bumptech.glide.Glide
import net.vrgsoft.library.LinkCrawler
import net.vrgsoft.library.LinkPreviewCallback
import net.vrgsoft.rxurlparser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LinkPreviewCallback {
    private lateinit var mBinding: ActivityMainBinding
    override fun onPre() {
        Toast.makeText(this, "Preload url", LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val crawler: LinkCrawler = LinkCrawler()
        crawler.mPreloadCallback = this
        crawler.parseUrl("https://github.com").subscribe({ t ->
            mBinding.content = t.result
        })

    }

}
