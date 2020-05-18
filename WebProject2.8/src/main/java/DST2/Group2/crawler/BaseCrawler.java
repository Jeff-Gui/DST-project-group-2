package DST2.Group2.crawler;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseCrawler {


    public String getURLContent(String urlString) {
        try {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = ((HttpURLConnection) url.openConnection());
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            InputStream inputStream = urlConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count = inputStream.read(buffer);
            while (count >= 0) {
                byteArrayOutputStream.write(buffer, 0, count);
                count = inputStream.read(buffer);
            }
            inputStream.close();
            return byteArrayOutputStream.toString();
        } catch (IOException e) {

        }
        return null;
    }

}
