#!/bin/bash
sftp $1:/C:/Users/EPB/Pictures/FotoWall/$2 <<EOF
lcd /home/epb1996/Pictures/FotoWall/$2
put *.jpg
exit
EOF