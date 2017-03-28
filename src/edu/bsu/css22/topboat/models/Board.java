package edu.bsu.css22.topboat.models;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public abstract class Board {
    public static final int HEIGHT = 10;
    public static final int WIDTH = 10;

    private static Board playerBoard = new Board() {
        @Override
        void onTileHit(Tile hitTile) {
            if(hitTile.isOccupied()) {
                Platform.runLater(() -> {
                    ImageView fireImageView = new ImageView(Tile.FIRE_IMAGE);
                    fireImageView.fitWidthProperty().bind(hitTile.widthProperty());
                    fireImageView.preserveRatioProperty().set(true);
                    hitTile.getChildren().add(fireImageView);
                });
            } else {
                Platform.runLater(() -> {
                    ImageView missImageView = new ImageView(Tile.MISS_IMAGE);
                    missImageView.fitWidthProperty().bind(hitTile.widthProperty());
                    missImageView.preserveRatioProperty().set(true);
                    hitTile.getChildren().add(missImageView);
                });
            }
        }
    };
    private static Board opponentBoard = new Board() {
        @Override
        void onTileHit(Tile hitTile) {
            hitTile.hasBeenHit = true;
            if(hitTile.isOccupied()) {
                Platform.runLater(() -> {
                    ImageView hitImageView = new ImageView(Tile.HIT_IMAGE);
                    hitImageView.fitWidthProperty().bind(hitTile.widthProperty());
                    hitImageView.preserveRatioProperty().set(true);
                    hitTile.getChildren().add(hitImageView);
                });
            } else {
                Platform.runLater(() -> {
                    ImageView missImageView = new ImageView(Tile.MISS_IMAGE);
                    missImageView.fitWidthProperty().bind(hitTile.widthProperty());
                    missImageView.preserveRatioProperty().set(true);
                    hitTile.getChildren().add(missImageView);
                });
            }
        }
    };

    private Tile[][] tileMap;
    public SimpleObjectProperty<Tile> selectedTileProperty = new SimpleObjectProperty<>();
    public SimpleObjectProperty<Tile> hoverTileProperty = new SimpleObjectProperty<>();

    private Board() {
        initTileMap();
    }

    private void initTileMap() {
        tileMap = new Tile[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                tileMap[y][x] = new Tile(this, x, y);
            }
        }
    }

    public static Board playerBoard() {
        return playerBoard;
    }

    public static Board opponentBoard() {
        return opponentBoard;
    }

    public Tile getTile(int x, int y) {
        try {
            return tileMap[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    abstract void onTileHit(Tile hitTile);


    public static class Tile extends StackPane {
        private static final Background OCEAN_BACKGROUND;
        static {
            CornerRadii radii = new CornerRadii(0);
            Insets insets = new Insets(0);
            BackgroundFill oceanFill = new BackgroundFill(Color.rgb(33, 103, 182), radii, insets);
            OCEAN_BACKGROUND = new Background(oceanFill);
        }
        private static final Image FIRE_IMAGE = new Image(Tile.class.getResourceAsStream("../images/fire.gif"));
        private static final Image MISS_IMAGE = new Image(Tile.class.getResourceAsStream("../images/miss.png"));
        private static final Image HIT_IMAGE = new Image(Tile.class.getResourceAsStream("../images/hit.png"));

        private Board board;
        public int x;
        public int y;
        public Ship ship;
        private ImageView imageView = new ImageView();
        public TileName name;
        private boolean hasBeenHit = false;

        private Tile(Board board, int x, int y) {
            setBackground(OCEAN_BACKGROUND);
            this.board = board;
            this.x = x;
            this.y = y;
            this.name = new TileName(x, y);

            addEventHandler(MouseEvent.MOUSE_ENTERED, event -> board.hoverTileProperty.set(this));
            addEventHandler(MouseEvent.MOUSE_EXITED, event -> board.hoverTileProperty.set(null));
            addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                board.selectedTileProperty.set(null);
                board.selectedTileProperty.set(this);
            });
        }

        public boolean isOccupied() {
            return ship != null;
        }

        public void setImage(Image newImage) {
            if(newImage == null) {
                getChildren().remove(imageView);
            } else {
                imageView.setImage(newImage);
                switch (ship.orientation) {
                    case UP:
                        imageView.setRotate(180);
                        imageView.fitHeightProperty().bind(this.heightProperty());
                        break;
                    case DOWN:
                        imageView.setRotate(0);
                        imageView.fitHeightProperty().bind(this.heightProperty());
                        break;
                    case LEFT:
                        imageView.setRotate(90);
                        imageView.fitHeightProperty().bind(this.widthProperty());
                        break;
                    case RIGHT:
                        imageView.setRotate(270);
                        imageView.fitHeightProperty().bind(this.widthProperty());
                        break;
                }
                getChildren().add(imageView);
            }
        }

        public boolean hasBeenHit() {
            return hasBeenHit;
        }

        public boolean hit() {
            board.onTileHit(this);
            return isOccupied();
        }

        public Board getBoard() {
            return board;
        }
    }

    public static class TileName {
        private static final HashMap<Integer, String> yValues;
        static {
            yValues = new HashMap<>();
            char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            for (int i = 0; i < HEIGHT; i++) {
                String value = String.valueOf(alphabet[i]);
                if (i > alphabet.length) {
                    value = value + i;
                }
                yValues.put(i, value);
            }
        }

        private String name;

        private TileName(int x, int y) {
            name = (yValues.get(y) + (x + 1));
        }

        @Override
        public String toString() {
            return name;
        }

    }
}
