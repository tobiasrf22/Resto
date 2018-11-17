from  pymongo import MongoClient
from Clases.Estadio import estadio
from Clases.Barrio import barrio
import urllib
import pprint
estadios = []
barrios = []
client = MongoClient('localhost', 27017)
db_estadio = client['Estadios']
posts_estadio = db_estadio.Estadios
l_estadio = posts_estadio.find().count()


for a in range(l_estadio):
    db = client['Estadios']
    posts = db.Estadios
    l = posts.find()
    for v in l:
        j = {}
        j.update(v)
        est =estadio(j['Estadio'],j['Pais'],j['Equipo'],j['Capacidad'],j['location']['coordinate'])
        estadios.append(est)
db_Barrios = client['Barrios']
posts_Barrios = db_Barrios.Barrios
l_Barrios = posts_Barrios.find().count()
for b in range(l_Barrios):
    db1 = client['Barrios']
    posts1 = db1.Barrios
    l1 = posts1.find()
    for v1 in l1:
        j1 = {}
        j1.update(v1)
        bar = barrio(j1['name'],j1['location']['coordinates'])
        barrios.append(bar)

