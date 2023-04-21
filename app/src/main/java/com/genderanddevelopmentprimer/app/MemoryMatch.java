package com.genderanddevelopmentprimer.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MemoryMatch extends AppCompatActivity {
    private final List<FrameLayout> imageViews = new ArrayList<>();
    private final List<String> imageLinks = new ArrayList<>();
    private FrameLayout firstSelected = null;
    private FrameLayout secondSelected = null;
    int score = 0;
    LinearLayout linearLayout;
    private CountDownTimer gameTimer;
    TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_match);

        GridLayout layout = findViewById(R.id.mm_layout);
        linearLayout = findViewById(R.id.ll_mm);
        timerTextView = new TextView(this);


        // Load images from the database
        loadImagesFromDatabase(layout);
    }

    private void loadImagesFromDatabase(GridLayout layout) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference imagesRef = db.collection("game").document("memoryMatch");
        imagesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Reset images if the game has already been played
                    resetImages(layout);

                    // Get the image URLs from the document data
                    int maxImages = 10;
                    int numImages = 0;

                    Random random = new Random();
                    List<Integer> randomIndexes = new ArrayList<>();
                    while (randomIndexes.size() < maxImages) {
                        int randomIndex = random.nextInt(documentSnapshot.getData().size()) + 1;
                        if (!randomIndexes.contains(randomIndex)) {
                            randomIndexes.add(randomIndex);
                        }
                    }

                    for (int i = 0; i < randomIndexes.size(); i++) {
                        if (numImages >= maxImages) {
                            break;
                        }
                        String field = "img" + randomIndexes.get(i);
                        if (documentSnapshot.contains(field)) {
                            String link = documentSnapshot.getString(field);
                            imageLinks.add(link);
                            imageLinks.add(link); // add each link twice
                            numImages++;
                        }
                    }

                    // Shuffle the list of image links
                    Collections.shuffle(imageLinks);

                    // Display the images in your app
                    for (int i = 0; i < imageLinks.size(); i++) {
                        // Create a FrameLayout to hold the image and the card overlay
                        CardView cardView = new CardView(getApplicationContext());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(8, 8, 8, 8);
                        cardView.setLayoutParams(layoutParams);
                        cardView.setTag(imageLinks.get(i));
                        cardView.setRadius(21);

                        // Create an ImageView to show the image
                        final ImageView imageView = new ImageView(getApplicationContext());
                        Picasso.get().load(imageLinks.get(i)).into(imageView);
                        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


                        // Create a card overlay to display on top of the image
                        final ImageView cardOverlay = new ImageView(getApplicationContext());
                        cardOverlay.setImageResource(R.drawable.card_back); // card_back is the drawable for the card overlay
                        cardOverlay.setLayoutParams(new FrameLayout.LayoutParams(
                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
                        cardOverlay.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        /*Drawable drawable = ContextCompat.getDrawable(MemoryMatch.this, R.drawable.logo);
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        // Scale the bitmap to a smaller size
                        float scaleFactor = 0.1f;
                        int scaledWidth = (int) (bitmap.getWidth() * scaleFactor);
                        int scaledHeight = (int) (bitmap.getHeight() * scaleFactor);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
                        // Set the scaled bitmap as the image drawable
                        cardOverlay.setImageDrawable(new BitmapDrawable(getResources(), scaledBitmap));
*/

                        timerTextView.setGravity(Gravity.CENTER);
                        timerTextView.setTextSize(18);
                        timerTextView.setPadding(0, 0, 0, 40);
                        // Start the game timer
                        long gameDurationMillis = 75000; // Set the game duration to 1 minute and 15 seconds
                        gameTimer = new CountDownTimer(gameDurationMillis, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                // Update the timer text view with the remaining time
                                long secondsRemaining = millisUntilFinished / 1000;
                                long minutes = secondsRemaining / 60;
                                long seconds = secondsRemaining % 60;
                                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                            }

                            @Override
                            public void onFinish() {
                                // Game over, show the result
                                showGameResult();
                            }
                        };

                        linearLayout.removeView(timerTextView);
                        linearLayout.addView(timerTextView);

                        cardView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Add the image and the card overlay to the FrameLayout
                                cardView.addView(cardOverlay);
                                gameTimer.start();
                            }
                        }, 12000);

                        // Add the FrameLayout to the layout and to the list of image views
                        cardView.addView(imageView);
                        layout.addView(cardView);
                        imageViews.add(cardView);

                        // Set an onClickListener for the FrameLayout
                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Ignore clicks on already matched images or when two images are already selected
                                if (cardView.getTag() == null || secondSelected != null || firstSelected == cardView) {
                                    return;
                                }
                                // Reveal the image
                                Picasso.get().load((String) cardView.getTag()).into(imageView);
                                cardOverlay.setVisibility(View.GONE);
                                // Set the first or second selected image view
                                if (firstSelected == null) {
                                    firstSelected = cardView;
                                } else {
                                    secondSelected = cardView;
                                }
                                // Check if two images are selected
                                if (secondSelected != null) {
                                    // Check if the images match
                                    if (firstSelected.getTag().equals(secondSelected.getTag())) {
                                        // Images match, remove them from the list of image views
                                        score++;
                                        imageViews.remove(firstSelected);
                                        imageViews.remove(secondSelected);

                                        // Reset the selected image views
                                        firstSelected = null;
                                        secondSelected = null;

                                        // Check if the game is over
                                        if (imageViews.size() == 0) {
                                            checkGameWon();
                                        }
                                    } else {
                                        // Images don't match, reset them after a short delay
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                firstSelected.getChildAt(1).setVisibility(View.VISIBLE);
                                                secondSelected.getChildAt(1).setVisibility(View.VISIBLE);
                                                // Reset the selected image views
                                                firstSelected = null;
                                                secondSelected = null;
                                            }
                                        }, 500);
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void showGameResult() {
        // Stop the game timer
        gameTimer.cancel();

        // Create a dialog to show the game result
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!");
        builder.setMessage("Final Score: " + score);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog and finish the activity
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void checkGameWon() {
        for (FrameLayout imageView : imageViews) {
            if (imageView.getTag() == null) {
                // If there is still an unmatched image, the game is not won
                return;
            }
        }
        // If all images are matched, show a dialog to indicate the game has been won
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!");
        builder.setMessage("You've matched all the images!");
        builder.setPositiveButton("Play Again?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Reset the game and load new images
                resetImages(findViewById(R.id.mm_layout));
                loadImagesFromDatabase(findViewById(R.id.mm_layout));
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exit the game
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void resetImages(GridLayout layout) {
        layout.removeAllViews();
        imageLinks.clear();
        imageViews.clear();
        firstSelected = null;
        secondSelected = null;
    }
}

