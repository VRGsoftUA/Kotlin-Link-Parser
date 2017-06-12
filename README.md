# Kotlin Link Parser
### Here is our implimentation of Link Preview written in Kotlin with RxKotlin2
## Usage
Initialize LinkCrawler
```kotlin
val crawler: LinkCrawler = LinkCrawler()
```
If you need to do somthing before parsing url, you can implement PreloadCallback
```kotlin
class MainActivity : AppCompatActivity(), LinkPreviewCallback

crawler.mPreloadCallback = this
```
To start parsing you need to use crawler.parseUrl and pass desired url, it returs ```Flowable<Result>```
```kotlin
  crawler.parseUrl("https://github.com").subscribe({ t ->
            mBinding.content = t.result
        })
 ```
 Result object contains ParseContent field wich contains all parsed data of passed url, such as title,description etc. 
  #### [Java version](https://github.com/VRGsoftUA/Java-Link-Parser/)
License
=================================

    Copyright 2016 VRG Soft

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
