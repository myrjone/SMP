#!/usr/bin/python

import random

dayofweek = ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday']
header = "first,last,email,day,08:00:00-10:00:00,10:00:00-12:00:00,12:00:00-14:00:00,14:00:00-16:00:00,16:00:00-18:00:00,18:00:00-20:00:00"

first = ['Travis', 'Mirian', 'Marshall', 'Marcia', 'Denisha', 'Albertine', 'Nan', 'Bradley', 'Herminia', 'Rossie', 'Eustolia', 'Leann', 'Vernita', 'Maren', 'Teresita', 'Patsy', 'Stanley', 'Sterling', 'Amparo', 'Cassaundra', 'Donnie', 'Luciano', 'Pamila', 'Maribel', 'Cynthia', 'Ava', 'Willy', 'Sharice', 'Latrice', 'Lupe', 'Emmie', 'Shavonne', 'Micheal', 'Callie', 'Darell', 'Cordell', 'Lashay', 'Len', 'Maile', 'Kasandra', 'Blake', 'Jeanett', 'Natisha', 'Antone', 'Julianna', 'Gustavo', 'Dagmar', 'Gussie', 'Myrtle', 'Kaylene']

last = ['Lillie', 'Janell', 'So', 'Merrilee', 'Jenee', 'Damaris', 'Isidro', 'Meridith', 'Vergie', 'Cleveland', 'Vicente', 'Whitney', 'Petrina', 'Margo', 'Dusty', 'Elmira', 'Tina', 'Glinda', 'Ai', 'Samuel', 'Tamar', 'Zonia', 'Kim', 'Floyd', 'Glen', 'Myrta', 'Etsuko', 'Minnie', 'Tobie', 'Vella', 'Anna', 'Lia', 'Hillary', 'Bao', 'Karole', 'Amado', 'Heath', 'Kiyoko', 'Pasquale', 'Tashina', 'Serina', 'Malinda', 'Isela', 'Quyen', 'Charmain', 'Albertine', 'Christa', 'Ronna', 'Branden', 'Lacey']

fileCounter = 0
for i in range(0,40):
    with open(first[i] + ".csv","w") as outfile:
        outfile.write(header)
        outfile.write("\n")

        for k in dayofweek:
            outfile.write(first[i])
            outfile.write(",")
            outfile.write(last[i])
            outfile.write(",")
            outfile.write(last[i] + "@siue.edu")
            outfile.write(",")
            outfile.write(k)
            for j in range(0,6):
                outfile.write(",")
                outfile.write(str(random.getrandbits(1)))
            outfile.write("\n")
    fileCounter = fileCounter + 1
