#!/usr/bin/env python3

import subprocess
import os.path
import sys
import json
from pathlib import Path

#"../../build/js/node_modules/gettext.js/bin/po2json"
basePath = "/Users/VIP/dev/github.com/a-cosmic-jaw/kvision-minimal-multi-module-project/build/js/node_modules" #os.environ['GOTHBUZZ_PATH'].split(",")[0]
import sys

print(sys.argv)

exit(1)

if not os.path.exists(basePath):
  Path(basePath).mkdir(parents=True, exist_ok=True)
  print("Created")
else:
  print("Exists")

os.chdir(basePath)

#main/src/jsMain/resources/i18n/messages-en.po
#main/src/jsMain/resources/i18n/messages-pl.po
#find main/src -name *po
#'/Users/VIP/dev/github.com/a-cosmic-jaw/gothbuzz/build/js/node_modules/gettext.js/bin/po2json

exit(1)



