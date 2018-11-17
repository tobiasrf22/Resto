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
        print(v['Estadio'],v['location'])