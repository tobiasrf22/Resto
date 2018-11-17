from  pymongo import MongoClient
from Clases.Estadio import estadio
from Clases.Barrio import barrio
import urllib
import pprint

client = MongoClient('localhost', 27017)
db = client['Estadios']
posts = db.Estadios
l = posts.find()
j = {}
for v in l:
    j.update(v)
est =estadio(j['Estadio'],j['Pais'],j['Equipo'],j['Capacidad'],j['location']['coordinate'])
print(est.cordinadas)
db1 = client['Barrios']
posts1 = db1.Barrios
l1 = posts1.find()
j1 = {}
for v1 in l1:
    j1.update(v1)
bar = barrio(j1['name'],j1['location']['coordinates'])
print(bar.cordinadas)

