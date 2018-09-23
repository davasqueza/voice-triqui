package co.edu.unal.triqui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    GameBoard gameBoard;

    private static final String DIGITS_SEARCH = "digits";
    private SpeechRecognizer recognizer;
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    @Override
    public void onPartialResult(Hypothesis hypothesis) { }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) { return;}

        String text = hypothesis.getHypstr();

        switch (text){
            case "uno":
                gameBoard.playTurn(1,1);
                break;
            case "dos":
                gameBoard.playTurn(1,2);
                break;
            case "tres":
                gameBoard.playTurn(1,3);
                break;
            case "cuatro":
                gameBoard.playTurn(2,1);
                break;
            case "cinco":
                gameBoard.playTurn(2,2);
                break;
            case "seis":
                gameBoard.playTurn(2,3);
                break;
            case "siete":
                gameBoard.playTurn(3,1);
                break;
            case "ocho":
                gameBoard.playTurn(3,2);
                break;
            case "nueve":
                gameBoard.playTurn(3,3);
                break;
            default:
                showToast("No te he entendido, por favor inténtalo de nuevo");
                break;
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    @Override
    public void onBeginningOfSpeech() { }

    @Override
    public void onEndOfSpeech() {
        reset();
    }

    private void setupRecognizer() {
        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        try {
            Assets assets = new Assets(MainActivity.this);
            File assetsDir = assets.syncAssets();

            recognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetsDir, "cmusphinx-es-5.2"))
                    .setDictionary(new File(assetsDir, "cmusphinx-es-5.2.dict"))
                    .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                    .getRecognizer();

            recognizer.addListener(this);

            File digitsGrammar = new File(assetsDir, "digits.gram");
            recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);

            reset();
        }
        catch (IOException e) {
            showToast("Hubo un error al intentar inicializar el reconocimiento por voz");
        }
    }

    private void reset() {
        recognizer.stop();
        recognizer.startListening(DIGITS_SEARCH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameBoard = new GameBoard(this);
        setContentView(gameBoard);

        setupRecognizer();
    }

    void showToast(String text){
        makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
