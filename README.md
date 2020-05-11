# SlideLeftMenu
左滑菜单

![Image text](1.png)



How to
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file


Add it in your root build.gradle at the end of repositories:


	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency


	dependencies {
	        implementation 'com.github.kuang2010:SlideLeftMenu:1.0.0'
	}
