package com.example.ceon.clickergame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Hero> heroes = new ArrayList<Hero>();
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    private ArrayList<Hero> ownedHeroes = new ArrayList<Hero>();
    private Player player = new Player(10, 5, 0, ownedHeroes);

    private int zone = 1;
    private int killCount = 0;
    private int killsRequired = 10;
    private float enemyHPStatus;

    private ListView list;

    // initialize player's activity
    private TextView playerMoney;
    private TextView playerFollowerDPS;
    private TextView playerClickDPS;

    // initialize enemy's activity
    private ImageButton enemyImage;
    private TextView enemyName;
    private TextView enemyHP;
    private ProgressBar enemyHealthBar;

    // initialize main activity
    private TextView zoneText;
    private TextView killsRequiredText;

    // Sounds
    private MediaPlayer purchaseSound;
    private MediaPlayer menuSound;
    private MediaPlayer mnsterSound1;
    private MediaPlayer mnsterSound2;
    private MediaPlayer mnsterSound3;
    private MediaPlayer mnsterSound4;
    private MediaPlayer mnsterSound5;
    private MediaPlayer bgm;

    Random generator = new Random();
    int i;

    private Enemy currEnemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize a service which will loop the BGM
        bgm = MediaPlayer.create(this, R.raw.bgm);
        bgm.setLooping(true);
        bgm.start();

        // find the list layout
        list = (ListView) findViewById(R.id.hero_list);

        // Initialize the audio files
        purchaseSound = MediaPlayer.create(this, R.raw.coin);
        menuSound = MediaPlayer.create(this, R.raw.menu);
        mnsterSound1 = MediaPlayer.create(this, R.raw.mnstr1);
        mnsterSound2 = MediaPlayer.create(this, R.raw.mnstr2);
        mnsterSound3 = MediaPlayer.create(this, R.raw.mnstr3);
        mnsterSound4 = MediaPlayer.create(this, R.raw.mnstr4);
        mnsterSound5 = MediaPlayer.create(this, R.raw.mnstr5);

        // find the properties on the activity
        playerMoney = (TextView) findViewById(R.id.player_money);
        playerFollowerDPS = (TextView) findViewById(R.id.player_f_dps);
        playerClickDPS = (TextView) findViewById(R.id.player_click_damage);

        enemyImage = (ImageButton) findViewById(R.id.enemy_image);
        enemyName = (TextView) findViewById(R.id.enemy_name);
        enemyHP = (TextView) findViewById(R.id.enemy_hp);
        enemyHealthBar = (ProgressBar) findViewById(R.id.enemy_hp_bar);

        zoneText = (TextView) findViewById(R.id.zone);
        killsRequiredText = (TextView) findViewById(R.id.req_kills);

        zoneText.setText("Zone " + String.valueOf(zone));
        killsRequiredText.setText(String.valueOf(killCount) + "/" + String.valueOf(killsRequired));

        populateHeroList();
        populateHeroView();
        populateEnemyList();
        clickCallBack();

        // Get the very first enemy of the game
        currEnemy = getRandomEnemy(enemies);
        currEnemy.setHealth(10 * ((zone - 1 + (int) Math.floor(Math.pow(1.55, zone - 1)))));
        currEnemy.setGold_yield(Math.ceil(Math.ceil(currEnemy.getHealth() / 15.0) * Math.min(3, Math.pow(1.025, (zone > 75) ? zone : 0 ))));
        setInitialEnemyProperties(currEnemy);

        enemyHPStatus = enemyHealthBar.getProgress();

        // Main game thread. The loop goes on forever
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        while(true) {
                            // Run this every 100ms
                            Thread.sleep(100);

                            // Divide the DPS by 10 for a gradual look of decrementing the HP bar
                            enemyHPStatus -= (float) (player.getFollowerDPS() / 10);

                            // Is the enemy dead?
                            if (enemyHPStatus <= 0) {
                                i = generator.nextInt(5) + 1;
                                // Ugly switch statement to get a random monster death sound
                                switch(i) {
                                    case 1:
                                        mnsterSound1.start();
                                        break;
                                    case 2:
                                        mnsterSound2.start();
                                        break;
                                    case 3:
                                        mnsterSound3.start();
                                        break;
                                    case 4:
                                        mnsterSound4.start();
                                        break;
                                    case 5:
                                        mnsterSound5.start();
                                        break;
                                }

                                killCount++;
                                if (killCount == killsRequired) {
                                    // Reset the kill count for the next zone
                                    killCount = 0;
                                    zone++;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            zoneText.setText("Zone " + String.valueOf(zone));
                                            killsRequiredText.setText(String.valueOf(killCount) + "/" + String.valueOf(killsRequired));
                                        }
                                    });
                                }

                                player.setMoney(player.getMoney() + (int) currEnemy.getGold_yield());
                                currEnemy = getRandomEnemy(enemies);

                                // Boss fight every 5 zones
                                if(zone % 5 == 0) {
                                    // Set the required kills to once for the boss fight
                                    killsRequired = 1;
                                    currEnemy = getRandomEnemy(enemies);

                                    // Buff the bosses stats for health and gold by 10 and 5, respectively
                                    currEnemy.setHealth(10 * ((zone - 1 + (int) Math.floor(Math.pow(1.55, zone - 1)))) * 10);
                                    currEnemy.setGold_yield(Math.ceil(Math.ceil(currEnemy.getHealth() / 15.0) * Math.max(3, Math.pow(1.025, (zone > 75) ? zone : 0))) * 5);
                                    currEnemy.setName("BOSS FIGHT");

                                    enemyHealthBar.setMax(currEnemy.getHealth());
                                    enemyHealthBar.setProgress(currEnemy.getHealth());
                                    enemyHPStatus = enemyHealthBar.getProgress();
                                }

                                // Otherwise retrieve a normal zone
                                else {
                                    // Revert the required kills to 10 for non-boss zones
                                    killsRequired = 10;
                                    currEnemy.setHealth(10 * ((zone - 1 + (int) Math.floor(Math.pow(1.55, zone - 1)))));
                                    currEnemy.setGold_yield(Math.ceil(Math.ceil(currEnemy.getHealth() / 15.0) * Math.max(3, Math.pow(1.025, (zone > 75) ? zone : 0))));

                                    enemyHealthBar.setMax(currEnemy.getHealth());
                                    enemyHealthBar.setProgress(currEnemy.getHealth());
                                    enemyHPStatus = enemyHealthBar.getProgress();
                                }

                                // This UI thread will update player info and the main UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        playerMoney.setText(String.valueOf(player.getMoney()));
                                        enemyImage.setImageResource(currEnemy.getIconID());
                                        enemyHP.setText(String.valueOf(currEnemy.getHealth()) + " HP");
                                        enemyName.setText(currEnemy.getName());
                                        killsRequiredText.setText(String.valueOf(killCount) + "/" + String.valueOf(killsRequired));
                                    }
                                });
                            }

                            // This UI thread will constantly update the enemy's HP
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    enemyHealthBar.setProgress((int) enemyHPStatus);
                                    enemyHP.setText(String.valueOf(enemyHealthBar.getProgress()) + " HP");
                                }
                            });
                        }
                    }
                    catch (Exception e) {
                        // Do nothing. The exception is needed so the thread can sleep
                    }
                }
        }).start();
    }

    // Populate the heroes List
    private void populateHeroList() {
        heroes.add(new Hero("Warrior", "Warrior's Description", R.drawable.follower1, 10, 1, 0));
        heroes.add(new Hero("Ranger", "Ranger's description", R.drawable.follower2, 10, 10, 0));
        heroes.add(new Hero("Assassin", "Assassin's description", R.drawable.follower3, 10, 10, 0));
        heroes.add(new Hero("Mage", "Mage's description", R.drawable.follower4, 15, 10, 0));
        heroes.add(new Hero("Monk", "Monk's description", R.drawable.follower5, 20, 10, 0));
        heroes.add(new Hero("Caster", "Caster's description", R.drawable.follower6, 50, 1000, 0));
        heroes.add(new Hero("Hero", "Hero's description", R.drawable.follower7, 150, 2000, 0));
        heroes.add(new Hero("Brawler", "Brawler's description", R.drawable.follower8, 500, 5000, 0));
        heroes.add(new Hero("Rune Master", "Rune Master's description", R.drawable.follower9, 1000, 15000, 0));
    }

    // Populate the enemy list
    private void populateEnemyList() {
        for(int i = 1; i < 170; i++) {
            String enemyName = "Monster " + i;
            int enemyImage = getResources().getIdentifier("enemy" + i, "drawable", getPackageName());
            enemies.add(new Enemy(enemyName, 1, enemyImage, 1));
        }
    }

    // Create the adapter for the heroes
    private void populateHeroView() {
        ArrayAdapter<Hero> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.hero_list);
        list.setAdapter(adapter);
    }


    // This class provides the list for the heroes
    private class MyListAdapter extends ArrayAdapter<Hero> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.hero_list_layout, heroes);
        }

        @NonNull
        @Override
        // Generate the actual view
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // Make sure there's a view to work with.
            View itemView = convertView;

            if (itemView == null) {
                // populate the list of heroes onto the layout
                itemView = getLayoutInflater().inflate(R.layout.hero_list_layout, parent, false);
            }

            final Hero currHero = heroes.get(position);

            // ------- Fill in the hero layout view ---------

            TextView nameText = (TextView) itemView.findViewById(R.id.hero_name);
            nameText.setText(currHero.getName());

            ImageView heroImage = (ImageView) itemView.findViewById(R.id.hero_image);
            heroImage.setImageResource(currHero.getIconID());

            TextView dpsText = (TextView) itemView.findViewById(R.id.hero_dps);
            dpsText.setText(String.valueOf(currHero.getTotalDps()));

            TextView levelText = (TextView) itemView.findViewById(R.id.hero_level);
            levelText.setText(String.valueOf(currHero.getLevel()));

            // -------  Fill in the main activity view --------
            playerFollowerDPS.setText(String.valueOf(player.getFollowerDPS()));
            playerMoney.setText(String.valueOf(player.getMoney()));
            playerClickDPS.setText(String.valueOf(player.getDamage()));

            ImageView backgroundImage = (ImageView) findViewById(R.id.background);
            backgroundImage.setImageResource(R.drawable.background);

            final Button purchase = (Button) itemView.findViewById(R.id.hero_buy);
            purchase.setText("HIRE\n" + currHero.getCost());

            // Listen for the purchase button
            purchase.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(player.getMoney() >= currHero.getCost()) {
                        if(!ownedHeroes.contains(currHero)) {
                            purchaseSound.start();
                            ownedHeroes.add(currHero);
                            player.buyHero(currHero);
                            purchase.setText("LVL UP" + currHero.getCost());
                            notifyDataSetChanged();
                        }
                        else {
                            purchaseSound.start();
                            player.buyHero(currHero);
                            notifyDataSetChanged();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "You don't have enough gold", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return itemView;
        }
    }

    // Click feedback
    private void clickCallBack() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Hero currHero = heroes.get(position);
                Intent intent = new Intent(MainActivity.this, HeroInfo.class);
                intent.putExtra("originalPosition", position);
                intent.putExtra("heroes", heroes);
                startActivity(intent);
            }
        });

        // Clicking on the enemy will do the player's clicker damage
        enemyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enemyHPStatus -= player.getDamage();
            }
        });
    }

    // Retrieve a random enemy from the array list
    private Enemy getRandomEnemy(ArrayList<Enemy> list) {
        Random rand = new Random();
        Enemy randEnemy = list.get(rand.nextInt(list.size()));
        return randEnemy;
    }

    // A testing function which may be useful later
    public void setInitialEnemyProperties(Enemy currEnemy) {
        enemyHealthBar.setMax(currEnemy.getHealth());
        enemyHealthBar.setProgress(currEnemy.getHealth());
        enemyImage.setImageResource(currEnemy.getIconID());
        enemyHP.setText(String.valueOf(currEnemy.getHealth()) + " HP");
        enemyName.setText(currEnemy.getName());
    }

}

