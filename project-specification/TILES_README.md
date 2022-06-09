# Path Tiles

The tiles used in the path are taken from the image [src/images/32x32GrassAndDirtPath.png](src/images/32x32GrassAndDirtPath.png)

The path is loaded within the source code at [/src/unsw/loopmania/LoopManiaWorldControllerLoader.java](/src/unsw/loopmania/LoopManiaWorldControllerLoader.java)

The description of each path tile, and coordinates in the above image, are in this below table (note that the (x, y) coordinates are taken from the top-left):

| Tile Type | Example | Coordinates (x, y) |
|:-------------:|:-------:|:---:|
|Non-path tile|![Tile 1][tile1]| (0, 0) |
|Vertical path tile|![Tile 2][tile2]| (32, 0) |
|Up then left/Right then down path tile|![Tile 3][tile3]| (0, 32) |
|Up then right/left then down path tile|![Tile 4][tile4]| (32, 32) |
|Down then right/left then up path tile|![Tile 5][tile5]| (0, 64) |
|Down then left/right then up path tile|![Tile 6][tile6]| (32, 64) |
|Horizontal path tile|![Tile 7][tile7]| (0, 96) |
|Round path tile (unused in starter-code)|![Tile 8][tile8]| (32, 96) |

[tile1]:                 examples/tile1.png
[tile2]:                 examples/tile2.png
[tile3]:                 examples/tile3.png
[tile4]:                 examples/tile4.png
[tile5]:                 examples/tile5.png
[tile6]:                 examples/tile6.png
[tile7]:                 examples/tile7.png
[tile8]:                 examples/tile8.png
