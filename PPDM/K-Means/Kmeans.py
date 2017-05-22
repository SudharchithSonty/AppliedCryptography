import numpy as np
import random
from sklearn.metrics import jaccard_similarity_score
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
import matplotlib.cm as cm
import pylab as pl

MAX_ITERATIONS = 10
k = input("Enter K-value: ")
filename = "/Users/Admin/Desktop/DataMining/Project-2/input/cho.txt"
groundtruth = []
clusterResult = []
orderedpairs = {}
inputdata = {}
initial_centroids = {}
clusterswithdistance = {}
centroid = {}
centroids = [3,5,9]


# Parse the input data from .txt file and store into key value pair where key is gene id and value is the list of expression values
def parse(s):
    count = 0
    result = {}
    with open(filename) as f:
        for row in f:
            count += 1
            row = row.strip().split()
            key = int(row[0])
            result[key] = row[2:]
    for x in result:
        final = [float(i) for i in result[x]]
        result[x] = final
    return result


#get the initial set of centroids for random values for the first iteration
def get_initial_centroids(inputdata, k):
    a = random.sample(inputdata, k)
    for i in xrange(0, k):
        if inputdata.__contains__(a[i]):
            key = i
            initial_centroids[key] = inputdata.get(a[i])
    return initial_centroids


# calculate the Euclidean Distance and assign the genes to their respective clusters
def assign_clusters_by_distance():
    clusterswithdistance.clear()
    for i in inputdata:
        list1 = inputdata.get(i)
        distancelist = []
        distances = {}
        for j in initial_centroids:
            list2 = initial_centroids.get(j)
            distance = calculate_distance(list1, list2)
            distancelist.append(distance)
            distances[distance] = j
        min_dist = min(distancelist)
        closestcluster = distances.get(min_dist)
        if clusterswithdistance.get(closestcluster):
            existing = []
            existing = clusterswithdistance.get(closestcluster)
            existing.append(i)
            clusterswithdistance[closestcluster] = existing
        else:
            firstone = [i]
            clusterswithdistance[closestcluster] = firstone
    return clusterswithdistance

# Compute the new centroids, by taking the mean of the old centroids and their corresponding gene values
def compute_new_centroids():
    initial_centroids.clear()
    sum_of_column = 0.0
    for cluster in clusterswithdistance.keys():
        geneids = clusterswithdistance.get(cluster)
        centroid = []
        listofallgenes = []
        for id in geneids:
            if inputdata.__contains__(id):
                listofallgenes.append(inputdata.get(id))
            else:
                print "Gene is not present"
        num_of_column = len(listofallgenes[0])
        for col in xrange(0, num_of_column):
            sum_of_column = 0.0
            for i in xrange(0, len(listofallgenes)):
                sum_of_column += listofallgenes[i][col];
            centroid.append(sum_of_column / len(listofallgenes))
        initial_centroids[cluster] = centroid
    return initial_centroids


# check if the centroids are converging
def check_termination(prev, current):
    for cluster1 in prev.keys():
        previous_centroids = prev.get(cluster1)
        current_centroids = current.get(cluster1)
        if not current_centroids == previous_centroids:
            return False
    return True

# funciton to get Euclidean Distance
def calculate_distance(list1, list2):
    sum = 0.0
    for i in xrange(0, len(list1)):
        sum += (list1[i] - list2[i]) ** 2
    distance = np.sqrt(sum)
    return distance

# function to calculate jaccard score
def get_jaccard_score():
    y_true = get_groundtruth(filename)
    y_pred = clusterResult
    return jaccard_similarity_score(y_true, y_pred)

# function to get the groundtruth values from file
def get_groundtruth(s):
    with open(filename) as f:
        for row in f:
            row = row.strip().split()
            groundtruth.append(int(row[1]))
    return groundtruth


# main function
inputdata = parse(filename)
initial_centroids = get_initial_centroids(inputdata, k)
clusterswithdistance = assign_clusters_by_distance()
converge =  False
iterations = 0
while iterations < MAX_ITERATIONS:
    iterations += 1
    previous_centroids = initial_centroids
    clusterswithdistance = assign_clusters_by_distance()
    new_initial_centroids = compute_new_centroids()
    converge = check_termination(previous_centroids, new_initial_centroids)
    if not converge:
        break
for cluster in  xrange(0, len(clusterswithdistance.keys())):
    print "Cluster " + str(cluster+1) + " " + "Size is " + str(len(clusterswithdistance.get(cluster))) + " and GeneId's are: " + str(clusterswithdistance.get(cluster))
for cluster in clusterswithdistance.keys():
    for id in clusterswithdistance.get(cluster):
        orderedpairs[id] = cluster+1
for i in orderedpairs.keys():
    clusterResult.append(orderedpairs.get(i))

print "Jaccard Coefficient is " + str(get_jaccard_score())
print iterations


#code to get the PCA
list = []
for key, value in inputdata.iteritems():
    list.append((value))
list = np.array(list)
y = np.array(clusterResult)
target_names = [0, 1, 2, 3, 4]
pca = PCA(n_components=2)
X_r = pca.fit(list).transform(list)
plt.figure()
plt.scatter(X_r[:, 0], X_r[:, 1], c=y, )
plt.legend()
plt.title('PCA')
plt.show()

