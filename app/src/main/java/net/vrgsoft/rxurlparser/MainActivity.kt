package net.vrgsoft.rxurlparser

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.disposables.Disposable
import net.vrgsoft.library.LinkCrawler
import net.vrgsoft.rxurlparser.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var mBinding: ActivityMainBinding
  private val crawler = LinkCrawler()

  private var githubSubscription: Disposable? = null
  private var youtubeSubscription: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    crawler.onPreload {
      Toast.makeText(this, "Preload url", Toast.LENGTH_SHORT)
          .show()
    }

    githubSubscription =
        crawler.parseUrl("https://github.com")
            .subscribe { t ->
              mBinding.contentGithub = t.result
            }

    youtubeSubscription =
        crawler.parseUrl("https://youtu.be/X1RVYt2QKQE")
            .subscribe { t ->
              mBinding.contentYoutube = t.result
            }
  }

  override fun onPause() {
    githubSubscription?.dispose()
    youtubeSubscription?.dispose()
    super.onPause()
  }
}
