package com.example.strataledger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InvoicingActivity extends AppCompatActivity {

    private static final String TAG = "InvoicingActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoicing);

        dbHelper = new DatabaseHelper(this);

        EditText invoiceNumberEditText = findViewById(R.id.invoiceNumberEditText);
        EditText invoiceDateEditText = findViewById(R.id.invoiceDateEditText);
        EditText clientNameEditText = findViewById(R.id.clientNameEditText);
        EditText amountEditText = findViewById(R.id.amountEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        Button sendInvoiceButton = findViewById(R.id.sendInvoiceButton);

        sendInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InvoicingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InvoicingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    createAndSendInvoice();
                }
            }

            public void createAndSendInvoice() {
                String invoiceNumber = invoiceNumberEditText.getText().toString();
                String invoiceDate = invoiceDateEditText.getText().toString();
                String clientName = clientNameEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                if (invoiceNumber.isEmpty() || invoiceDate.isEmpty() || clientName.isEmpty() || amount.isEmpty() || description.isEmpty()) {
                    Toast.makeText(InvoicingActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean success = dbHelper.addInvoice(invoiceNumber, invoiceDate, clientName, amount, description);
                    if (success) {
                        String filePath = generateInvoicePdf(invoiceNumber, invoiceDate, clientName, amount, description);
                        if (filePath != null) {
                            Toast.makeText(InvoicingActivity.this, "Invoice Sent and Saved at: " + filePath, Toast.LENGTH_SHORT).show();
                            // Optionally, clear the input fields
                            invoiceNumberEditText.setText("");
                            invoiceDateEditText.setText("");
                            clientNameEditText.setText("");
                            amountEditText.setText("");
                            descriptionEditText.setText("");
                        } else {
                            Toast.makeText(InvoicingActivity.this, "Failed to generate invoice", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InvoicingActivity.this, "Failed to send invoice", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private String generateInvoicePdf(String invoiceNumber, String invoiceDate, String clientName, String amount, String description) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(12);
        canvas.drawText("Invoice", 10, 10, paint);
        canvas.drawText("Invoice Number: " + invoiceNumber, 10, 30, paint);
        canvas.drawText("Invoice Date: " + invoiceDate, 10, 50, paint);
        canvas.drawText("Client Name: " + clientName, 10, 70, paint);
        canvas.drawText("Amount: " + amount, 10, 90, paint);
        canvas.drawText("Description: " + description, 10, 110, paint);

        pdfDocument.finishPage(page);

        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        String filePath = directoryPath + "/Invoice_" + invoiceNumber + ".pdf";

        File file = new File(filePath);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Log.d(TAG, "PDF created at: " + filePath);
        } catch (IOException e) {
            Log.e(TAG, "Error writing PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            pdfDocument.close();
        }

        return filePath;
    }
}
