#!/usr/bin/env python3

import subprocess
import re
import os.path
import sys
import json
from pathlib import Path

print(sys.argv)
basePath = sys.argv[1]
mainname = "kvision-minimal-multi-module-project"
subname = "addressbook-fullstack-micronaut"

for path in [basePath + "build/js/packages/kvision-minimal-multi-module-project-addressbook-fullstack-micronaut/kotlin/i18n", basePath + "build/js/packages/kvision-minimal-multi-module-project-addressbook-fullstack-micronaut/kotlin/css", basePath + "/build/js/packages/"+mainname+"-" + subname + "/webpack/bin"]:
  if not os.path.exists(path):
    Path(path).mkdir(parents=True, exist_ok=True)

for sourcePath, destPath in [(basePath + "/"+subname+"/webpack.config.d/webpack.js", basePath + "/build/js/packages/"+mainname+"-" + subname + "/webpack/bin/webpack.js")]:
  command = "cp " + sourcePath + " " + destPath
  print("Runnink command: " + command)
  for line in subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.readlines():
    print("cpoyoutput" + str(line))
