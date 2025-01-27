package flutter.plugins.screen.screen;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * ScreenPlugin
 */
public class ScreenPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {


  private MethodChannel channel;
  private Activity activity;
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "github.com/clovisnicolas/flutter_screen");
    this.context = flutterPluginBinding.getApplicationContext();
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch(call.method){
      case "brightness":
        result.success(getBrightness());
        break;
      case "setBrightness":
        double brightness = call.argument("brightness");
        WindowManager.LayoutParams layoutParams = this.activity.getWindow().getAttributes();
        layoutParams.screenBrightness = (float)brightness;
        this.activity.getWindow().setAttributes(layoutParams);
        result.success(null);
        break;
      case "isKeptOn":
        int flags = this.activity.getWindow().getAttributes().flags;
        result.success((flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0) ;
        break;
      case "keepOn":
        Boolean on = call.argument("on");
        if (on) {
          System.out.println("Keeping screen on ");
          this.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else{
          System.out.println("Not keeping screen on");
          this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        result.success(null);
        break;

      default:
        result.notImplemented();
        break;
    }
  }

  private float getBrightness(){
    float result = this.activity.getWindow().getAttributes().screenBrightness;
    if (result < 0) { // the application is using the system brightness
      try {
        result = Settings.System.getInt(this.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / (float)255;
      } catch (Settings.SettingNotFoundException e) {
        result = 1.0f;
        e.printStackTrace();
      }
    }
    return result;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {

  }
}
