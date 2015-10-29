#!/usr/bin/python

import random
import os.path
import sys

sectionLetters = ['A','B']
dayofweek = ['U','M','T','W','R','F','S']
header = "CRN,DEPT,CRS,SEC,DAY,START,END,BLDG,RM,COORD"
coordinators = ['Shabestary', 'Patrick', 'Voss', 'Jones', 'Khazaeli', 'Luesse', "O'Brien", 'Navarre', 'Wei']        

filename = raw_input("Enter a filename: ")
if (os.path.isfile(filename)):
    print "That file exists already"
    sys.exit(1)

with open(filename,"w") as outfile:
    outfile.write(header)
    outfile.write("\n")

    dept = "CHEM"
    for j in range(1,12):
        for item in sectionLetters:
            crsPrefix = random.randint(100,400)
            secMax = random.randint(1,8)
            crs = str(crsPrefix) + item
            coord = random.choice(coordinators)
            for i in range(1,secMax):
                crn = random.randint(10000,19999)
                section = '%03d' % i
                day = random.choice(dayofweek)
                start = random.randint(8,16)
                startString = ('%02d' % start ) + "00"
                end = start + 1
                endString = ('%02d' % end) + "50"
                bldg = "SLW"
                rm = random.randint(2200,3300)

                outfile.write(str(crn) + ",")
                outfile.write(dept + ",")
                outfile.write(crs + ",")
                outfile.write(section + ",")
                outfile.write(day + ",")
                outfile.write(startString + ",")
                outfile.write(endString + ",")
                outfile.write(bldg + ",")
                outfile.write(str(rm) + ",")
                outfile.write(coord)
                outfile.write("\n")
