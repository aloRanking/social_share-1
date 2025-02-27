package com.shekarmudaliyar.social_share

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import androidx.core.content.FileProvider
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File

class SocialSharePlugin:FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var activeContext: Context? = null
    private var context: Context? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "social_share")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        activeContext = if (activity != null) activity!!.applicationContext else context!!

        if (call.method == "shareInstagramStory") {
            //share on instagram story
            val stickerImage: String? = call.argument("stickerImage")
            val backgroundImage: String? = call.argument("backgroundImage")

            val backgroundTopColor: String? = call.argument("backgroundTopColor")
            val backgroundBottomColor: String? = call.argument("backgroundBottomColor")
            val attributionURL: String? = call.argument("attributionURL")
            val file =  File(activeContext!!.cacheDir,stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)

            val intent = Intent("com.instagram.share.ADD_TO_STORY")
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("interactive_asset_uri", stickerImageFile)
            if (backgroundImage!=null) {
                //check if background image is also provided
                val backfile =  File(activeContext!!.cacheDir,backgroundImage)
                val backgroundImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", backfile)
                intent.setDataAndType(backgroundImageFile,"image/*")
            }
            intent.putExtra("content_url", attributionURL)
            intent.putExtra("top_background_color", backgroundTopColor)
            intent.putExtra("bottom_background_color", backgroundBottomColor)
            Log.d("", activity!!.toString())
            // Instantiate activity and verify it will resolve implicit intent
            activity!!.grantUriPermission("com.instagram.android", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activity!!.packageManager.resolveActivity(intent, 0) != null) {
                activeContext!!.startActivity(intent)
                result.success("success")
            } else {
                result.success("error")
            }
        } else if (call.method == "sharedInstagram") {

            val stickerImage: String? = call.argument("stickerImage")
            val attributionURL: String? = call.argument("attributionURL")
            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
            val intent = Intent(Intent.ACTION_SEND)
            Log.d("log", stickerImage!!)
            var media = stickerImage?.endsWith("mp4")
            if (media == true) {
                intent.type = "video/*"
                Log.d("log", "video done")
                // Log.d("log", stickerImage)
            } else {
                intent.type = "image/*"
                Log.d("log", "image set")
                // Log.d("log", stickerImage)
            }

            intent.setPackage("com.instagram.android")
            intent.putExtra(Intent.EXTRA_STREAM, stickerImageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
            intent.putExtra("content_url", attributionURL)
            Log.d("", activity!!.toString())
            // Instantiate activity and verify it will resolve implicit intent
            //val activity: Activity = registrar.activity()
            activity!!.grantUriPermission("com.instagram.android", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activity!!.packageManager.resolveActivity(intent, 0) != null) {
                activeContext!!.startActivity(intent)
                result.success("success")
            } else {
                result.success("error")
            }

        } else if (call.method == "shareFacebookStory") {
            //share on facebook story
            val stickerImage: String? = call.argument("stickerImage")
            val backgroundTopColor: String? = call.argument("backgroundTopColor")
            val backgroundBottomColor: String? = call.argument("backgroundBottomColor")
            val attributionURL: String? = call.argument("attributionURL")
            val appId: String? = call.argument("appId")

            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
            val intent = Intent("com.facebook.stories.ADD_TO_STORY")
            intent.type = "image/*"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appId)
            intent.putExtra("interactive_asset_uri", stickerImageFile)
            intent.putExtra("content_url", attributionURL)
            intent.putExtra("top_background_color", backgroundTopColor)
            intent.putExtra("bottom_background_color", backgroundBottomColor)
            Log.d("", activity!!.toString())
            // Instantiate activity and verify it will resolve implicit intent
            activity!!.grantUriPermission("com.facebook.katana", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activity!!.packageManager.resolveActivity(intent, 0) != null) {
                activeContext!!.startActivity(intent)
                result.success("success")
            } else {
                result.success("error")
            }
        } else if (call.method == "shareFacebookFeed") {

            val stickerImage: String? = call.argument("stickerImage")
            val content: String? = call.argument("content")
            val appId: String? = call.argument("appId")

            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
            val intent = Intent(Intent.ACTION_SEND)
            //intent.type = "*/*"
            var media = stickerImage?.endsWith("mp4")
            if (media == true) {
                intent.type = "video/*"
                Log.d("log", "video done")
                // Log.d("log", stickerImage)
            } else {
                intent.type = "image/*"
                Log.d("log", "image set")
                // Log.d("log", stickerImage)
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appId)

            intent.putExtra("content_url", content)
            intent.putExtra(Intent.EXTRA_STREAM, stickerImageFile);
            Log.d("", activity!!.toString())
            // Instantiate activity and verify it will resolve implicit intent
            //val activity: Activity = registrar.activity()
            var facebookAppFound = false

            val matches: List<ResolveInfo> = activity!!.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (info in matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana") ||
                        info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.lite")) {
                    intent.setPackage(info.activityInfo.packageName)
                    // intent.setClassName(info.activityInfo.packageName, "com.facebook.lite.ShareLinkActivity" )
                    facebookAppFound = true
                    break
                }
            }

            if (facebookAppFound) {
                activeContext!!.startActivity(intent)
                result.success("success")
            } else {
                //showWarningDialog(appCompatActivity, appCompatActivity.getString(R.string.error_activity_not_found));
                result.success("error")

            }


        } else if (call.method == "shareOptions") {
            //native share options
            val content: String? = call.argument("content")
            val image: String? = call.argument("image")
            val intent = Intent()
            intent.action = Intent.ACTION_SEND

            if (image != null) {
                //check if  image is also provided
                val imagefile = File(activeContext!!.cacheDir, image)
                val imageFileUri = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
            } else {
                intent.type = "text/plain";
            }

            intent.putExtra(Intent.EXTRA_TEXT, content)

            //create chooser intent to launch intent
            //source: "share" package by flutter (https://github.com/flutter/plugins/blob/master/packages/share/)
            val chooserIntent: Intent = Intent.createChooser(intent, null /* dialog title optional */)
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            activeContext!!.startActivity(chooserIntent)
            result.success(true)

        } else if (call.method == "copyToClipboard") {
            //copies content onto the clipboard
            val content: String? = call.argument("content")
            val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", content)
            clipboard.setPrimaryClip(clip)
            result.success(true)
        } else if (call.method == "shareWhatsapp") {
            //shares content on WhatsApp
            val content: String? = call.argument("content")
            val stickerImage: String? = call.argument("stickerImage")
            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "*/*"
            //whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, content)
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, stickerImageFile);
            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            var whatsAppFound = false
            val matches: List<ResolveInfo> = activity!!.getPackageManager().queryIntentActivities(whatsappIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (info in matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.whatsapp") ||
                        info.activityInfo.packageName.toLowerCase().startsWith("com.yowhatsapp") ||
                        info.activityInfo.packageName.toLowerCase().startsWith("com.gbwhatsapp")) {
                    whatsappIntent.setPackage(info.activityInfo.packageName)
                    whatsAppFound = true
                    break
                }
            }

            if (whatsAppFound) {
                activity!!.startActivity(whatsappIntent)
                result.success("success")
            } else {
                //showWarningDialog(appCompatActivity, appCompatActivity.getString(R.string.error_activity_not_found));
                result.success("error")

            }

        } else if (call.method == "shareTelegram") {
            //shares content on Telegram
            val content: String? = call.argument("content")
            val stickerImage: String? = call.argument("stickerImage")
            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
            val telegramIntent = Intent(Intent.ACTION_SEND)
            telegramIntent.type = "*/*"
            telegramIntent.setPackage("org.telegram.messenger")
            telegramIntent.putExtra(Intent.EXTRA_TEXT, content)
            telegramIntent.putExtra(Intent.EXTRA_STREAM, stickerImageFile);
            telegramIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            telegramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                activity!!.startActivity(telegramIntent)
                result.success("true")
            } catch (ex: ActivityNotFoundException) {
                result.success("false")
            }
        } else if (call.method == "shareSms") {
            //shares content on sms
            val content: String? = call.argument("message")
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.type = "vnd.android-dir/mms-sms"
            intent.data = Uri.parse("sms:")
            intent.putExtra("sms_body", content)
            try {
                activity!!.startActivity(intent)
                result.success("true")
            } catch (ex: ActivityNotFoundException) {
                result.success("false")
            }
        } else if (call.method == "shareTwitter") {
            val stickerImage: String? = call.argument("stickerImage")
            val text: String? = call.argument("captionText")
            val url: String? = call.argument("url")
            val trailingText: String? = call.argument("trailingText")
            val file = File(activeContext!!.cacheDir, stickerImage)
            val stickerImageFile = FileProvider.getUriForFile(activeContext!!, activeContext!!.applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)

            val intent = Intent(Intent.ACTION_SEND)

            intent.setPackage("com.twitter.android")
            //intent.setClassName("com.twitter.android", "com.twitter.android.composer.ComposerActivity" )

            //intent.type = "text/plain"
            /*intent.putExtra(Intent.EXTRA_TEXT, trailingText)
            intent.putExtra(Intent.EXTRA_TEXT, url)*/
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, stickerImageFile);
            intent.putExtra(Intent.EXTRA_TEXT, text)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //  Log.d("log",urlScheme)

            activity!!.grantUriPermission("com.twitter.android", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (activity!!.packageManager.resolveActivity(intent, 0) != null) {
                activeContext!!.startActivity(intent)
                result.success("success")
            } else {

                result.success("error")
            }

        }

        /*
        else if (call.method == "shareTwitter") {
            //shares content on twitter
            val text: String? = call.argument("captionText")
            val url: String? = call.argument("url")
            val trailingText: String? = call.argument("trailingText")
            val urlScheme = "http://www.twitter.com/intent/tweet?text=$text$url$trailingText"
            Log.d("log",urlScheme)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(urlScheme)
            try {
                activity!!.startActivity(intent)
                result.success("true")
            } catch (ex: ActivityNotFoundException) {
                result.success("false")
            }
        }*/
        else if (call.method == "checkInstalledApps") {
            //check if the apps exists
            //creating a mutable map of apps
            var apps:MutableMap<String, Boolean> = mutableMapOf()
            //assigning package manager
            val pm: PackageManager =context!!.packageManager
            //get a list of installed apps.
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            //intent to check sms app exists
            val intent = Intent(Intent.ACTION_SENDTO).addCategory(Intent.CATEGORY_DEFAULT)
            intent.type = "vnd.android-dir/mms-sms"
            intent.data = Uri.parse("sms:" )
            val resolvedActivities: List<ResolveInfo>  = pm.queryIntentActivities(intent, 0)
            //if sms app exists
            apps["sms"] = resolvedActivities.isNotEmpty()
            //if other app exists
            apps["instagram"] = packages.any { it.packageName.toString().contentEquals("com.instagram.android") }
            apps["facebook"] = packages.any { it.packageName.toString().contentEquals("com.facebook.katana") }
            apps["twitter"] = packages.any { it.packageName.toString().contentEquals("com.twitter.android") }
            apps["whatsapp"] = packages.any { it.packageName.toString().contentEquals("com.whatsapp") }
            apps["telegram"] = packages.any { it.packageName.toString().contentEquals("org.telegram.messenger") }

            result.success(apps)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.getActivity()
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }
}