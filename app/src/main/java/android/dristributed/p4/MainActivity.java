package android.dristributed.p4;

import android.content.Context;
import android.dristributed.p4.model.Const;
import android.dristributed.p4.model.Game;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    LinearLayout mMainLayout;
    volatile GraphicBoard mGraphicBoard;

    public static int pixelToDip(int pixelValue, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelValue, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGraphicBoard = new GraphicBoard();
                drawGame();
                Snackbar.make(view, "New Game !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mMainLayout = (LinearLayout) findViewById(R.id.main_rel_layout);
        mGraphicBoard = new GraphicBoard();
        drawGame();
        mMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float percent = 1.f * event.getX() / v.getWidth();
                    Log.i("P4", "percent: " + percent);
                    mGraphicBoard.onTouchDown(percent);
                    return true;
                }
                return false;
            }
        });
    }

    public void drawGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMainLayout.removeAllViews();
                mMainLayout.addView(mGraphicBoard.createBoardView());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GraphicBoard {
        private final Game game = new Game(3);

        public void onTouchDown(float percent) {
            byte row = (byte) Math.floor(Const.ROW_NBR * percent);
            Log.i("P4", "row: " + row);
            if (game.dropDisc(row) != Const.FORBIDDEN_MOVE){
                drawGame();
            }
        }

        public View createBoardView() {

            int width = pixelToDip(750, MainActivity.this);
            int height = pixelToDip(648, MainActivity.this);

            // Create a bitmap with the dimensions we defined above, and with a 16-bit pixel format. We'll
            // get a little more in depth with pixel formats in a later post.
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Create a paint object for us to draw with, and set our drawing color to red.


            // Create a new canvas to draw on, and link it to the bitmap that we created above. Any drawing
            // operations performed on the canvas will have an immediate effect on the pixel data of the
            // bitmap.
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.parseColor("#3f51b5"));

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);

            for (int i = 0; i < Const.ROW_NBR; i++) {
                for (int j = 0; j < Const.LINE_NBR; j++) {
                    int xmargin = i + 1;
                    int ymargin = j + 1;
                    int xcenter = i * 90 + xmargin * 15 + 45;
                    int ycenter = j * 90 + ymargin * 15 + 45;
                    byte discValue = game.getBoard().getCellAt(j, i);
                    paint.setColor(discValueToColor(discValue));
                    canvas.drawCircle(pixelToDip(xcenter, MainActivity.this), pixelToDip(ycenter, MainActivity.this), pixelToDip(43, MainActivity.this), paint);
                }
            }

            // In order to display this image in our activity, we need to create a new ImageView that we
            // can display.
            ImageView imageView = new ImageView(MainActivity.this);

            // Set this ImageView's bitmap to the one we have drawn to.
            imageView.setImageBitmap(bitmap);

            // Create a simple layout and add our image view to it.
            RelativeLayout layout = new RelativeLayout(MainActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(imageView, params);
            //layout.setBackgroundColor(Color.BLACK);

            return layout;
        }

        public int discValueToColor(byte discValue) {
            switch (discValue) {
                case Const.EMPTY_CELL:
                    return Color.BLACK;
                case 0:
                    return Color.parseColor("#ffeb3b"); //YELLOW
                case 1:
                    return Color.parseColor("#f44336"); //RED
                case 2:
                    return Color.parseColor("#4caf50"); //GREEN
            }
            return Color.WHITE;
        }

    }
}
