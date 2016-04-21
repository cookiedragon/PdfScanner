package de.cookiedragon.pdfscanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;


public class PdfCreator {

	private Context context;

	public PdfCreator(Context context) {
		this.context = context;
	}

	public boolean createPdf(List<Uri> uris) {

		if (uris.isEmpty()) {
			return false;
		}

		boolean success = false;
		PdfDocument document = new PdfDocument();

		for (int i = 0; i < uris.size(); i++) {

			Uri path = uris.get(i);
			PageInfo pageInfo = new PageInfo.Builder(100, 100, i + 1).create();
			Page page = document.startPage(pageInfo);

			int width = pageInfo.getPageWidth();
			int height = pageInfo.getPageHeight();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path.getPath(), options);
			int photoWidth = options.outWidth;
			int photoHeight = options.outHeight;

			int scaleFactor = Math
					.min(photoWidth / width, photoHeight / height);

			options.inJustDecodeBounds = false;
			options.inSampleSize = scaleFactor;
			options.inPurgeable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(path.getPath(), options);
			page.getCanvas().drawColor(Color.WHITE);
			page.getCanvas().drawBitmap(bitmap, 1, 1, null);

			document.finishPage(page);
		}

		File file = new File(context.getExternalFilesDir(null),
				"PdfScanner.pdf");
		try {
			document.writeTo(new FileOutputStream(file));
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
		return success;
	}
}
