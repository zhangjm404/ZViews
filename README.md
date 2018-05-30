# ZViews
将 Android 开发时常用的一些组件封装成库，方便其他项目使用；
目前有 SplashFragme 

## 导入项目

- ```
  compile 'com.github.zhangjm404:ZViews:0.3.1'
  ```

## SplashFrame 闪屏控件 
简单高效的闪屏控件，一行代码即可调起。
有以下几个优点
- 使用方式简单,支持多种样式
- 支持缓存广告图片
- 支持自定义倒计时按钮
- 支持点击事件；
### 效果图
![img](./img/splashFrame.gif)
### 使用方法
```java
//TODO 弹出闪屏
SplashFrame.show(this, R.mipmap.ic_launcher, new SplashFrame.OnSplashActionListener() {
            @Override
            public void onImageClick(String event, String target) {
                Log.e(TAG, event + "   -----   " + target);
            }

            @Override
            public void onHide() {

            }
        });

//TODO 缓存闪屏数据，在任意地方均可调用
SplashModel splashModel = new SplashModel("http://5b0988e595225.cdn.sohucs.com/images/20180312/7239efc4c9cf46e68a144748f8010af6.jpeg","event","target");
        SplashFrame.cacheData(this, splashModel);
        
//TODO 自定义跳过按钮
1.继承BaseSkipTextView
2. 调用这个show 方法
	/**
     * 弹出闪屏页
     *
     * @param activity
     * @param bottomImgRes  下面的 logo 图res 资源；如果传入-1则使用全屏幕闪屏默认
     * @param listener      闪屏页监听器
     * @param downCountTime 倒计时时长，单位：秒
     * @param skipTextView  自定义倒计时按钮，继承BaseSkipTextView即可
     */
    public static void show(Activity activity, int bottomImgRes, OnSplashActionListener listener, int downCountTime, BaseSkipTextView skipTextView)；
```