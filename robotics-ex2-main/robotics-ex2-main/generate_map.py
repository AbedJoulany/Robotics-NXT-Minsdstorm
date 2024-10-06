import numpy as np
import math, json, struct

def create_map_file(points, start, end, center):
    with open("map.bin", 'wb') as file:
        file.write(struct.pack('>i', len(points)))
        for p in points.tolist():
            print(p)
            file.write(struct.pack('>f', round(p[0], 1)))
            file.write(struct.pack('>f', round(p[1], 1)))


        file.write(struct.pack('>f', round(start[1][0], 1)))
        file.write(struct.pack('>f', round(start[1][1], 1)))
        file.write(struct.pack('>f', round(end[1][0], 1)))
        file.write(struct.pack('>f', round(end[1][1], 1)))
        file.write(struct.pack('>f', round(center[0], 1)))
        file.write(struct.pack('>f', round(center[1], 1)))

        print(f"center {center[0]}, {center[1]}")

        map_file = open("map.json", "w")
        json.dump({
            'points': np.round(points, 1).tolist(),
            'start': np.round(start[1], 1).tolist(),
            'end': np.round(end[1], 1).tolist(),
            'center': np.round(center, 1).tolist()
        }, map_file, indent=6)



def read_points_from_file(filename):
    points = []
    with open(filename, 'rb') as file:
        num_points = struct.unpack('>i', file.read(4))[0]
        print(f'Number of Points: {num_points}')

        for _ in range(num_points):
            x = struct.unpack('>f', file.read(4))[0]
            y = struct.unpack('>f', file.read(4))[0]
            points.append((x, y))
            print(f'Point: ({x}, {y})')

        startx = struct.unpack('>f', file.read(4))[0]
        starty = struct.unpack('>f', file.read(4))[0]
        endx = struct.unpack('>f', file.read(4))[0]
        endy = struct.unpack('>f', file.read(4))[0]
        centerx = struct.unpack('>f', file.read(4))[0]
        centery = struct.unpack('>f', file.read(4))[0]

        print(f'Start Point: ({startx}, {starty})')
        print(f'End Point: ({endx}, {endy})')
        print(f'Center Point: ({centerx}, {centery})')


target_points = np.array([
    [0, 0],
    [-101.0, 0],
    [-190.0, 89.0],
    [-190, 237],
    [0, 237]
])
target_center =np.array([-95, 118.5])
"""
target_points = np.array([
    [0, 0],
    [-113.0, 0],
    [-113.0, 40.0],
    [-78, 77],
    [0, 77]
])
target_center =np.array([-96, 58])
"""
start_p =  [[-45 , 0],[-43, 10]]
end_p = [[-55,0], [-55,10]]

create_map_file(target_points, start_p, end_p, target_center)
read_points_from_file('map.bin')

