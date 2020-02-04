#!/bin/bash
sftp -oPort=2410 $1:/C:/Users/EPB/Pictures/FotoWall/$2 <<EOF
lcd /home/epb1996/Pictures/FotoWall/$2
mput *.jpg
exit
EOF
rm -rf /home/epb1996/Pictures/FotoWall/$2/*.jpg