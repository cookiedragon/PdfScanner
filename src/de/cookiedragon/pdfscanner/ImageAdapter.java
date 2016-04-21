package de.cookiedragon.pdfscanner;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context context;
	private List<Uri> picturePaths;

	public ImageAdapter(Context context, List<Uri> picturePaths) {
		this.context = context;
		this.picturePaths = picturePaths;
	}

	public int getCount() {
		return picturePaths.size();
	}

	public Object getItem(int position) {
		return picturePaths.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new GridView.LayoutParams(
					parent.getWidth() / 3, parent.getHeight() / 3));

		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setImageURI(picturePaths.get(position));
		return imageView;
	}
}
