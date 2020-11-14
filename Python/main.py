# Generates magnetic field data for the network

import numpy as np
import matplotlib.pyplot as plt
from scipy.spatial.transform import Rotation as R
import pickle as pkl

class samples:
    mu = 0
    angle = np.empty([2])
    translation = np.empty([3])

    def __init__(self, numPoints):
        self.positions = np.empty([numPoints, 3])
        self.forces = np.empty([numPoints, 3])

def spc(x, y, z):
    r = np.sqrt(x**2 + y**2 + z**2)
    phi = np.arctan(y / x)
    theta = np.arccos(z / r)
    r_out = r*np.array([np.sin(theta)*np.cos(phi), np.sin(theta)*np.sin(phi), np.cos(theta)])
    return r_out

def B(mu, x, y, z):
    r = np.sqrt(x**2 + y**2 + z**2)
    phi = np.arctan(y/x)
    theta = np.arccos(z/r)

    # r_vec = spc(x, y, z)
    #
    # r_hat = r_vec / r
    # theta_hat = np.array([np.cos(phi)*np.cos(theta), np.sin(phi)*np.cos(theta), -np.sin(theta)])
    #
    # B = mu/r**3 * (r_hat*2*np.cos(theta) + theta_hat*np.sin(theta))


    B = 3*mu/(r**3) * np.array([np.cos(phi), np.sin(phi), np.cos(theta)**2-1/3])
    B[0] = B[0]*np.sin(theta) * np.cos(theta)
    B[1] = B[1]*np.sin(theta) * np.cos(theta)

    return B

def pm():
    rand = np.random.uniform(0, 1, 1)
    if rand < 0.5:
        return -1
    else:
        return 1


def Rx(theta):
    return np.array([[1, 0, 0],
                      [0, np.cos(theta), -np.sin(theta)],
                      [0, np.sin(theta), np.cos(theta)]])


def Ry(theta):
    return np.array([[np.cos(theta), 0, np.sin(theta)],
                      [0, 1, 0],
                      [-np.sin(theta), 0, np.cos(theta)]])


def Rz(theta):
    return np.array([[np.cos(theta), -np.sin(theta), 0],
                      [np.sin(theta), np.cos(theta), 0],
                      [0, 0, 1]])

def genSample(number, numSamples, mu_min, mu_max):
    mus = np.random.uniform(mu_min, mu_max, number)
    sampleSet = np.empty([number], dtype=samples)
    for i in range(number):
        sampleSet[i] = samples(numSamples)
        sampleSet[i].mu = mus[i]
        offset = np.array([pm()*np.random.uniform(10, 100, 1),
                           pm()*np.random.uniform(10, 100, 1),
                           pm()*np.random.uniform(10, 100, 1)])

        translate = np.array(np.random.uniform(-100, 100, 3))

        sampleSet[i].translation = translate

        angles = np.array(np.random.uniform(0, 2*np.pi, 2))

        sampleSet[i].angle = angles

        for j in range(numSamples):
            point = np.array([np.random.uniform(-15, 15, 1),
                           np.random.uniform(-15, 15, 1),
                           np.random.uniform(-15, 15, 1)])
            point += offset
            sampleSet[i].forces[j] = B(mus[i], point[0], point[1], point[2])[0]

            pointTrans = point[0] + translate

            rotZ = Rz(angles[0])
            rotX = Rx(angles[1])
            pointRotTrans = rotX.dot(rotZ.dot(pointTrans))

            sampleSet[i].positions[j] = pointRotTrans

    return sampleSet


if __name__ == "__main__":
    # mu = 10
    # n = 50
    # x = np.linspace(-10, 10, n)
    # y = np.linspace(-10, 10, n)
    # z = np.linspace(-10, 10, n)
    #
    # xx, yy, zz = np.meshgrid(x, y, z)
    #
    # bs = B(mu, xx, yy, zz)
    # plt.quiver(x, z, bs[1, 8, :, :], bs[2, 8, :, :])
    # # plt.imshow(np.abs(bs[1, :, :, 0]))
    # plt.show()

    sam = genSample(1000, 100, 1, 10)
    file = "./genDat1"
    with open(file, "wb") as f:
        pkl.dump(sam, f)


