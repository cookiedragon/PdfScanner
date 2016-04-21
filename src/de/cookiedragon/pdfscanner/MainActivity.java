package de.cookiedragon.pdfscanner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String FILE_BLANK = "_";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private GridView gallery;
	private PdfCreator pdfCreator;

	private List<Uri> picturePaths = new ArrayList<Uri>();
	private Uri lastPicture = null;

	private void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File image;
			try {
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(new Date());
				String imageFileName = JPEG_FILE_PREFIX + timeStamp
						+ FILE_BLANK;
				File folder = getExternalFilesDir(null);
				image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
						folder);
				lastPicture = Uri.fromFile(image);
				takePictureIntent
						.putExtra(MediaStore.EXTRA_OUTPUT, lastPicture);
			} catch (IOException e) {
				Log.d(getClass().getName(), "Could not create Image File");
				image = null;
			}
			if (image != null) {
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			} else {
				lastPicture = null;
				Log.d(getClass().getName(),
						"Could not take picture because the file was null");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			if (lastPicture != null) {
				picturePaths.add(lastPicture);
				lastPicture = null;
				updateGallery();
			}
		}
	}

	private void updateGallery() {
		gallery.invalidateViews();
		gallery.setAdapter(new ImageAdapter(this, picturePaths));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		pdfCreator = new PdfCreator(this);

		gallery = (GridView) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(this, picturePaths));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

			}
		});

	}

	protected void toast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.newImage) {
			toast("Wish to get a new Image");
			dispatchTakePictureIntent();
			return true;
		} else if (id == R.id.createPdf) {
			boolean success = pdfCreator.createPdf(picturePaths);
			if (success) {
				toast("Pdf created");
				for (Uri path : picturePaths) {
					File file = new File(path.getPath());
					file.delete();
				}
				picturePaths.clear();
				updateGallery();
			} else {
				toast("Nope! Not happening");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
