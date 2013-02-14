package org.nosreme.app.urlhelper.test;

import java.lang.reflect.Field;

import org.nosreme.app.urlhelper.ActionChooser;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.mock.MockPackageManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ActionChooserIsolatedTest extends
		ActivityUnitTestCase<ActionChooser> {
	
	/* Simple class for returning the full result from an activity. */
	public class ActivityResult {
		public int code;
		public Intent data;
		public ActivityResult(int c, Intent d) {
			code = c;
			data = d;
		}
	}
	
	public ActionChooserIsolatedTest() {
		super(ActionChooser.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();

	}

	public void testPreConditions() {
	    /* Nothing yet to test */
	}

	protected ActivityResult getResult(Activity activity)
	{
		/* Thanks to 
		 * http://stackoverflow.com/questions/5569830/get-result-from-an-activity-after-finish-in-an-android-unit-test
		 * for this way of finding the activity result.  There *must* be a
		 * better way... */
		try {
			Field f = Activity.class.getDeclaredField("mResultCode");
			f.setAccessible(true);
			int actualResultCode = (Integer)f.get(activity);
			f = Activity.class.getDeclaredField("mResultData");
			f.setAccessible(true);
			Intent realResult = (Intent)f.get(activity);
			return new ActivityResult(actualResultCode, realResult);
		} catch (NoSuchFieldException e) {
			assert false;
			return null;
		} catch (Exception e) {
			assert false;
			return null;
		}
		
	}
	
	public void testSimple() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);

	    ActionChooser activity = startActivity(intent, null, null);
	    
	    final Button okButton = (Button)activity.findViewById(org.nosreme.app.urlhelper.R.id.choose_open);
	    
	    //okButton.requestFocus();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				okButton.performClick();
			}
		});
	    
		getInstrumentation().waitForIdleSync();
		
		ActivityResult result = getResult(activity);
		assertEquals(result.code, Activity.RESULT_OK);	  
	}

	public void testVisible() throws Throwable {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
		runTestOnUiThread(new Runnable() {
		        public void run() {
		            startActivity(intent, null, null);
		        }
		});
	    ActionChooser activity = getActivity(); //startActivity(intent, null, null);
	    
	    final CheckBox ruleCb = (CheckBox)activity.findViewById(org.nosreme.app.urlhelper.R.id.check_addrule);
	    View regex_label = activity.findViewById(org.nosreme.app.urlhelper.R.id.title_ruleregex);
	    View regex_entry = activity.findViewById(org.nosreme.app.urlhelper.R.id.multi_ruleregex);

	    /* Assume it starts unchecked */
	    assertFalse(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	    
		activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		
   		/* They should now be visible */
	    assertTrue(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.VISIBLE);
	    assertEquals(regex_entry.getVisibility(), View.VISIBLE);

	    activity.runOnUiThread(new Runnable() {
			public void run() {
				ruleCb.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();

   		/* And invisible again */
	    assertFalse(ruleCb.isChecked());
	    assertEquals(regex_label.getVisibility(), View.GONE);
	    assertEquals(regex_entry.getVisibility(), View.GONE);
	    
	    /* Now check that the "Open with..." spinner is enabled at the right
	     * times. */
	    final Spinner spinner = (Spinner)activity.findViewById(org.nosreme.app.urlhelper.R.id.spinner_openwith);
	    RadioButton openRad = (RadioButton)activity.findViewById(org.nosreme.app.urlhelper.R.id.radio_openwith);
	    final RadioButton expandRad = (RadioButton)activity.findViewById(org.nosreme.app.urlhelper.R.id.radio_expand);
	    assertNotNull(openRad);
	    assertNotNull(spinner);
	    /* Should start with this one selected */
	    assertTrue(openRad.isChecked());
	    assertFalse(expandRad.isChecked());
	    assertTrue(spinner.isEnabled());
	    
	    /* Select another radio button, and check the spinner is disabled. */
	    activity.runOnUiThread(new Runnable() {
			public void run() {
				expandRad.performClick();
			}
		});
   		getInstrumentation().waitForIdleSync();
   		
   		assertFalse(openRad.isChecked());
	    assertTrue(expandRad.isChecked());
	    assertFalse(spinner.isEnabled());
	    
	    /* Now check that the spinner has suitable entries */
	    assertTrue(spinner.getCount() == 2);	    
	}

	public void testCancel() {
		Context context = this.getInstrumentation().getTargetContext().getApplicationContext();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com/"), context, ActionChooser.class);
		
	    ActionChooser activity = startActivity(intent, null, null);
	    getInstrumentation().waitForIdleSync();

	    sendKeys(KeyEvent.KEYCODE_BACK);

	    getInstrumentation().waitForIdleSync();

		ActivityResult result = getResult(activity);
		assertEquals(result.code, Activity.RESULT_CANCELED);	  

	}
}