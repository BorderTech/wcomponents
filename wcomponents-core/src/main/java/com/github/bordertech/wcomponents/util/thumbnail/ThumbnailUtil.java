package com.github.bordertech.wcomponents.util.thumbnail;

// Standard Java Libraries.
import com.github.bordertech.wcomponents.ImageResource;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class used for thumbnails.
 *
 * @author Jonathan Austin
 */
public final class ThumbnailUtil {

	/**
	 * Default thumbnail size. Scale to 64 high.
	 */
	private static final Dimension THUMBNAIL_SCALE_SIZE = new Dimension(-1, 64);

	/**
	 * Default thumbnail size.
	 */
	private static final Dimension THUMBNAIL_DEFAULT_SIZE = new Dimension(64, 64);

	/**
	 * Thumbnail for ms-word.
	 */
	public static final ImageResource THUMBNAIL_MSWORD = new ImageResource(
			"/icons/thumbnails/application-msword.png",
			"msword", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for pdf.
	 */
	public static final ImageResource THUMBNAIL_PDF = new ImageResource(
			"/icons/thumbnails/application-pdf.png", "pdf",
			THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for ms-excel.
	 */
	public static final ImageResource THUMBNAIL_MSEXCEL = new ImageResource(
			"/icons/thumbnails/application-vnd.ms-excel.png",
			"msexcel", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for ms-powerpoint.
	 */
	public static final ImageResource THUMBNAIL_MSPPT = new ImageResource(
			"/icons/thumbnails/application-vnd.ms-powerpoint.png",
			"mspowerpoint", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for compress.
	 */
	public static final ImageResource THUMBNAIL_COMPRESS = new ImageResource(
			"/icons/thumbnails/application-x-compress.png",
			"compress", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for audio.
	 */
	public static final ImageResource THUMBNAIL_AUDIO = new ImageResource(
			"/icons/thumbnails/audio-x-generic.png",
			"audio", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for generic image.
	 */
	public static final ImageResource THUMBNAIL_IMAGE = new ImageResource(
			"/icons/thumbnails/image-x-generic.png",
			"image", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for misc.
	 */
	public static final ImageResource THUMBNAIL_MISC = new ImageResource(
			"/icons/thumbnails/misc.png", "misc",
			THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for text.
	 */
	public static final ImageResource THUMBNAIL_TEXT = new ImageResource(
			"/icons/thumbnails/text-x-generic.png",
			"text", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * Thumbnail for video.
	 */
	public static final ImageResource THUMBNAIL_VIDEO = new ImageResource(
			"/icons/thumbnails/video-x-generic.png",
			"video", THUMBNAIL_DEFAULT_SIZE);

	/**
	 * The logger instance for this class.
	 */
	private static final Log LOG = LogFactory.getLog(ThumbnailUtil.class);

	/**
	 * The "format" passed to {@link ImageIO} to indicate that it should create a JPEG image.
	 */
	private static final String IMAGE_JPEG_FORMAT = "jpeg";

	/**
	 * Don't allow this utility class to be constructed.
	 */
	private ThumbnailUtil() {
		// NO-OP.
	}

	/**
	 * This method takes a input document (represented by an {@link InputStream}) and returns a byte[] representing a
	 * JPEG "thumb nail" of a given page of the document. It can do this for a limited number of input document
	 * "types"images.
	 *
	 * @param is The {@link InputStream} representing the input document.
	 * @param name The name of the file from which the input document was sourced.
	 * @param scaledSize the size to which the given <em>image</em> is to be scaled, null for default
	 * @param mimeType the mime type
	 * @return a byte[] array representing a JEPG thumb nail of the specified page within the Office document or Image.
	 */
	public static com.github.bordertech.wcomponents.Image createThumbnail(final InputStream is,
			final String name,
			final Dimension scaledSize, final String mimeType) {
		final Dimension scale = scaledSize == null ? THUMBNAIL_SCALE_SIZE : scaledSize;

		// Generate thumbnail for image files
		if (is != null && mimeType != null && (mimeType.equals("image/jpeg") || mimeType.equals(
				"image/bmp")
				|| mimeType.equals("image/png") || mimeType.equals("image/gif"))) {
			byte[] bytes = createImageThumbnail(is, scale);
			if (bytes != null) {
				return new BytesImage(bytes, "image/jpeg", "Thumbnail of " + name, null);
			}
		}

		// Use default thumbnail depending on mime type
		com.github.bordertech.wcomponents.Image image = handleDefaultImage(mimeType, name, scale);
		return image;
	}

	/**
	 * @param mimeType the files mime type
	 * @param name the file name
	 * @param scale the thumbnail size
	 * @return the thumbnail
	 */
	private static com.github.bordertech.wcomponents.Image handleDefaultImage(final String mimeType,
			final String name,
			final Dimension scale) {
		com.github.bordertech.wcomponents.Image image;
		if (mimeType == null) {
			image = THUMBNAIL_MISC;
		} else if (mimeType.equals("application/pdf")) {
			image = THUMBNAIL_PDF;
		} else if (mimeType.equals("application/msword")) {
			image = THUMBNAIL_MSWORD;
		} else if (mimeType.equals("application/vnd.ms-excel")) {
			image = THUMBNAIL_MSEXCEL;
		} else if (mimeType.equals("application/vnd.ms-powerpoint")) {
			image = THUMBNAIL_MSPPT;
		} else if (mimeType.equals("application/zip")) {
			image = THUMBNAIL_COMPRESS;
		} else if (mimeType.startsWith("text")) {
			image = THUMBNAIL_TEXT;
		} else if (mimeType.startsWith("audio")) {
			image = THUMBNAIL_AUDIO;
		} else if (mimeType.startsWith("video")) {
			image = THUMBNAIL_VIDEO;
		} else if (mimeType.startsWith("image")) {
			image = THUMBNAIL_IMAGE;
		} else {
			LOG.error("Unrecognised mime type [" + mimeType + "]");
			image = THUMBNAIL_MISC;
		}

		// Check if we need to adjust the provided default thumbnail size
		boolean sameHeight = scale.height == -1 || scale.height == THUMBNAIL_DEFAULT_SIZE.height;
		boolean sameWidth = scale.width == -1 || scale.width == THUMBNAIL_DEFAULT_SIZE.width;
		if (!sameHeight || !sameWidth) {
			// Scale to correct size
			ByteArrayInputStream byteIs = new ByteArrayInputStream(image.getBytes());
			byte[] bytes = createImageThumbnail(byteIs, scale);
			image = new BytesImage(bytes, "image/jpeg", "Thumbnail of " + name, null);
		}
		return image;
	}

	/**
	 * This method will create a JPEG "thumb nail" of an image read from an {@link InputStream}. The maximum Dimension
	 * of the returned JPEG Image will be {@link #THUMBNAIL_MAX}.
	 *
	 * @param is the InputStream representing the image for which the JPEG thumb nail is to be returned.
	 * @param scaledSize the size to which the given <em>image</em> is to be scaled.
	 * @return a byte[] representing the JPEG thumb nail.
	 */
	private static byte[] createImageThumbnail(final InputStream is, final Dimension scaledSize) {
		BufferedImage image;
		MemoryCacheImageInputStream mciis;

		try {
			mciis = new MemoryCacheImageInputStream(is);
			image = ImageIO.read(mciis);
		} catch (Exception e) {
			LOG.warn("Unable to read input image", e);
			return null;
		}

		if (image == null) {
			return null;
		}

		try {
			byte[] jpeg = createScaledJPEG(image, scaledSize);
			return jpeg;
		} catch (Exception e) {
			LOG.error("Error creating thumbnail from image", e);
		} finally {
			image.flush();
		}

		return null;

	}

	/**
	 * This method creates an array of bytes representing a JPEG image that is a "scaled" version of the given
	 * {@link Image}.
	 *
	 * @param image The image to be turned into a scaled JPEG.
	 * @param scaledSize The size to which the given <em>image</em> is to be scaled.
	 * @return A byte[] representing the JPEG image containing the scaled {@link Image}.
	 * @throws IOException on any sort of error.
	 */
	private static byte[] createScaledJPEG(final Image image, final Dimension scaledSize) throws
			IOException {
		// Scale the image.
		Image scaledImage = image.getScaledInstance(scaledSize.width, scaledSize.height,
				Image.SCALE_SMOOTH);

		// Create a BufferedImage copy of the scaledImage.
		BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.
				getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.drawImage(scaledImage, 0, 0, null);
		scaledImage.flush();
		graphics.dispose();

		// Convert the scaled image to a JPEG byte array.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		MemoryCacheImageOutputStream mciis = new MemoryCacheImageOutputStream(baos);
		ImageIO.write(bufferedImage, IMAGE_JPEG_FORMAT, mciis);
		mciis.flush();
		bufferedImage.flush();

		byte[] jpeg = baos.toByteArray();
		mciis.close();

		return jpeg;
	}

}
