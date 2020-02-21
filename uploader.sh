#!/bin/bash
sftp -oPort=2410 $1:MagPi/MagicMirror/modules/MMM-RandomBackground/photos <<EOF
lcd /home/epb1996/Pictures/FotoWall/$2
put *.jpg
exit
EOF
rm -rf /home/epb1996/Pictures/FotoWall/$2/*.jpg