from  pymongo import MongoClient
from Clases.Estadio import estadio
from Clases.Barrio import barrio
import urllib
import pprint
estadios = []
barrios = []
client = MongoClient('localhost', 27017)
for a in range(20):
    db = client['Estadios']
    posts = db.Estadios
    l = posts.find()
    for v in l:
        j = {}
        j.update(v)
        est =estadio(j['Estadio'],j['Pais'],j['Equipo'],j['Capacidad'],j['location']['coordinate'])
        estadios.append(est)
for b in range(6):
    db1 = client['Barrios']
    posts1 = db1.Barrios
    l1 = posts1.find()
    for v1 in l1:
        j1 = {}
        j1.update(v1)
        bar = barrio(j1['name'],j1['location']['coordinates'])
        barrios.append(bar)

