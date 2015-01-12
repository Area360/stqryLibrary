package nz.co.stqry.library.volley.network;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


public class FileRequest extends Request<File> {

    private static final String TAG = FileRequest.class.getSimpleName();

    private String mFileName;
    private boolean mInternalStorage;
    private Context mContext;

    private Response.Listener<File> mListener;
    private Map<String, String> mHeader;

    public FileRequest(int method, String url, String fileName, boolean internalStorage, Context context, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mListener = listener;
        mHeader = new HashMap<String, String>();

        mFileName = fileName;
        mInternalStorage = internalStorage;
        mContext = context;

    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        String responseString = new String(response.data);


        String filePath = "";
        File createdFile = null;

        if (mInternalStorage) {
            filePath = mContext.getFilesDir() + "/" + mFileName;
        } else {
            //TODO check file storage state
            filePath = Environment.getExternalStorageDirectory() + "/" + mFileName;
        }
        createdFile = bytesToFile(response.data, filePath);
        return Response.success(createdFile, getCacheEntry());
    }

    private File bytesToFile(byte[] data, String filePath) {
        InputStream input = null;
        OutputStream output = null;
        File createdFile = new File(filePath);

        try {

            output = new FileOutputStream(filePath);
            input = new ByteArrayInputStream(data);

            byte readData[] = new byte[4096];
            int count;

            while ((count = input.read(readData)) != -1) {
                output.write(readData, 0, count);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createdFile;
    }

    private File stringToFile(String responseString, String filePath) {

        File createdFile = null;

        try {
            createdFile = new File(filePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(createdFile));
            outputStreamWriter.write(responseString);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

        return createdFile;
    }

    private void printFile(File file) {

        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            try {
                while ((ch = fis.read()) != -1)
                    fileContent.append((char) ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, fileContent.toString());
    }

    @Override
    protected void deliverResponse(File response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
}
