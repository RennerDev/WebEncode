WebEncode
==============
[![Build Status](https://travis-ci.org/RennerDev/WebEncode.svg?branch=master)](https://travis-ci.org/RennerDev/WebEncode)
[![Code Climate](https://codeclimate.com/github/RennerDev/WebEncode/badges/gpa.svg)](https://codeclimate.com/github/RennerDev/WebEncode)
[![Test Coverage](https://codeclimate.com/github/RennerDev/WebEncode/badges/coverage.svg)](https://codeclimate.com/github/RennerDev/WebEncode/coverage)
[![Issue Count](https://codeclimate.com/github/RennerDev/WebEncode/badges/issue_count.svg)](https://codeclimate.com/github/RennerDev/WebEncode)


Workflow
========

To compile the entire project, run "mvn install".

To run the application, run "mvn jetty:run" and open http://localhost:8080/ .

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean package"
- test the war file with "mvn jetty:run-war"

