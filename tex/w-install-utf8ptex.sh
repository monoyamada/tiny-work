#! /bin/sh
#  w-install-utf8ptex.sh
#  2011-05-30 Mon 03:20:54  watahiki@a.email.ne.jp
#  $Id: w-install-utf8ptex.sh,v 0.119 2011-06-01 20:46:21+09 watahiki Exp watahiki $
# 
 

   # set -x

   # [0]
   PRG=`basename $0`
   DiR=`dirname $0`
    WD=`pwd`
    export PRG
    export DiR
    export WD

   # [0.1]
   EXEC=0
   HELP=1
   if [ $# -gt 0 ]; then
      if [ "x-x" = "x$1" ]; then
         EXEC=1
         HELP=0
      elif [ "x-s" = "x$1" ]; then
         EXEC=0
         HELP=0
      elif [ "x-h" = "x$1" ]; then
         EXEC=0
         HELP=1
      fi
   fi


   TEXLIVE_TOP=/usr/local/texlive
         #     sample11506.tex
          BASE=sample11506



   # [1]
   echo
   echo "#  +-------------------------------------+"
   echo "#  |        platex の インストール       |"
   echo "#  +-------------------------------------+"
   if [ $HELP -eq 1 ]; then
      Id=
      xx=
      echo
      echo "#  +---------------------------------------------+"
      echo "#  | 構文                                        |"
      echo "#  |    $0   [オプション]"
      echo "#  | オプション                                  |"
      echo "#  |       -s :  非実行モード                    |"
      echo "#  |       -x :  実行モード                      |"
      echo "#  | 例                                          |" 
      echo "#  |    chmod +x $0"
      echo "#  |    $0  -s"
      echo "#  |    $0  -x"
      echo "#  |    $0  -h"
      echo "#  +---------------------------------------------+"
      echo "#  $Id: w-install-utf8ptex.sh,v 0.119 2011-06-01 20:46:21+09 watahiki Exp watahiki $xx"
      echo
      exit 0
   fi

   # [1.1]
   if [ -d $TEXLIVE_TOP/bin ]; then
      echo
      echo "#   +---------------------------------------+"
      echo "#   |                                       |"
      echo "#   |             texlive の                |"
      echo "#   |     インストールは済んでいます        |"
      echo "#   |                                       |"
      echo "#   +---------------------------------------+"
      echo
      exit 0
   fi

   # [1.2]
   echo "#  +-------------------------------------+"
   echo "#  |                                     |"
   if [ $EXEC -eq 1 ]; then
   echo "#  |    動作モード : 実行モード          |"
   else
   echo "#  |    動作モード : *非*実行モード      |"
   fi
   echo "#  |                                     |"
   echo "#  +-------------------------------------+"
   echo


    
w_msg() {
   cat <<EOF

 #  +--------------------------------------------------------+
 #  |                   *非*実行モード(-s)                   |
 #  |                     での処理でした                     |
 #  |    (実行モードでの処理スクリプトを表示しただけです)    |
 #  +--------------------------------------------------------+
 #
 #  +--------------------------------------------------------+
 #  |                    実行するには                        |
 #  |             ./w-install-utf8ptex.sh  -x
 #  |                   と、して下さい                       |
 #  |                 インストールします                     |
 #  +--------------------------------------------------------+

EOF
}







   # [1]
w_install_utf8platex() {
   cat <<EOF| grep -v "^#" | grep -v "^\s*$"
   wait(){
      sleep 2;
      echo -n "## [\$1]  |---------+---------+:\r"
      echo -n "## [\$1]  |"
      echo -n "=>"; sleep 1
      for T in   1 2 3 4 5 6 7 8 9 ; do
         echo -n "\b==>"; sleep 1
      done
      echo "|"; sleep 1
   }
   wait2(){
      sleep 2;
      echo -n "## [\$1]  |---------+---------+---------+:\r"
      echo -n "## [\$1]  |"
      echo -n "=>"; sleep 1
      echo -n "=>"; sleep 1
      for T in   1 2 3 4 5 6 7 8 9  10 11 12 13 14 ; do
         echo -n "\b==>"; sleep 1
      done
      echo "|"; sleep 1
   }
EOF



   cat <<EOF| grep -v "^#" | grep -v "^\s*$"
   # [0] 準備
   test ! -x /usr/bin/wget     && sudo apt-get -y install wget
   test ! -x /usr/bin/xz       && sudo apt-get -y install xz-utils
   test ! -x /usr/bin/xwd      && sudo apt-get -y install x11-apps
   test ! -x /usr/bin/convert  && sudo apt-get -y install imagemagick
EOF


   #   ubuntu 11.04 で utf-8 の通る platex を
   #  インストール  
   cat <<EOF| grep -v "^#" | grep -v "^\s*$"

      # [2.1]
      echo "# [2.1] utf8-ptex.tar の取得"
      test ! -f utf8-ptex.tar && wget  http://w0.dyndns.org/oxo/utf8ptex-test/utf8-ptex.tar

      echo "# [2.2] utf8-ptex.tar の展開"
      test ! -d utf8-ptex &&   tar xvf utf8-ptex.tar

      echo "#  [3] texlive.tar.xz の展開"
      cd utf8-ptex
         test ! -d texlive && tar xvf texlive.tar.xz
         sudo mv   texlive  /usr/local/. 
         echo "#  +------------------------------------+"
         echo "#  |                                    |"
         echo "#  |   これでインストールの完了です     |"
         echo "#  |                                    |"
         echo "#  +------------------------------------+"
      cd ..
         echo
         echo "#  +----------------------------------------+"
         echo "#  |   アンインストールは                   |"
         echo "#  |     utf8-ptex/w-uninstall-utf8ptex.sh"
         echo "#  +----------------------------------------+"
         echo
         echo "#  +------------------------------------+"
         echo "#  |   動作チェックは                   |"
         echo "#  |     utf8-ptex/Test/x-test.sh  -x"
         echo "#  +------------------------------------+"
EOF
}


   
   if [ $EXEC -eq 0 ]; then
      w_install_utf8platex
      w_msg
   else
      echo -n "#  utf-8版の platex をインストールしますか [yes/No]: "
      read yesNo
      if [ "_$yesNo" != "_yes" ]; then
         echo
         echo "#  +-------------------------------------+"
         echo "#  |                                     |"
         echo "#  |      インストールを中断します       |"
         echo "#  |                                     |"
         echo "#  +-------------------------------------+"
         echo
         exit 0
      else
         echo "#  +------------------------------------------------+"
         echo "#  |           インストールを開始します             |"
         echo "#  |------------------------------------------------|"
         echo "#  |              sudo を使います                   |"
         echo "#  |           password の入力要求があります        |"
         echo "#  |        (処理時間がかかると再度の要求あり)      |"
         echo "#  +------------------------------------------------+"
         sudo pwd >/dev/null 2>&2
         w_install_utf8platex | sh
      fi
   fi

   exit 0
#
