import numpy as np


class AreaMap:
    def __init__(self, vertices, line_1, line_2):
        self.vertices = vertices
        self.line_1 = line_1
        self.line_2 = line_2

    def generate_map(self):
        P = self.generate_transformation_matrix()
        points = self.apply_transformation(P, self.vertices)
        lines = self.apply_transformation(P,  np.array([self.line_1, self.line_2]))
        center = self.find_center( self.vertices)
        #print(  points)
        center =  self.apply_transformation(P,  np.array([center]))

        print(center)
        return {
            'points': points,
            'start': np.array( [[lines[0][0],0], [lines[0][0],8]] ),
            'end': np.array( [[lines[1][0],0], [lines[1][0], 8]] ),
            'center': center[0]
        }


    def find_center(self, points):
        min_x, min_y = np.min(points, axis=0)
        max_x, max_y = np.max(points, axis=0)

        center_x = (min_x + max_x) / 2
        center_y = (min_y + max_y) / 2

        return np.array([center_x, center_y])
       # return np.mean(points, axis=0)

    def generate_transformation_matrix(self):
        angle = self.calc_required_rotation()
        #print(angle)
        anchor_point = self.vertices[-2]

        R = np.array([
            [np.cos(angle), -np.sin(angle), 0],
            [np.sin(angle), np.cos(angle), 0],
            [0, 0, 1]
        ])
        T = np.array([
            [1, 0, -anchor_point[0]],
            [0, 1, -anchor_point[1]],
            [0, 0, 1]
        ])
        M = R.dot(T)
        return M

    def apply_transformation(self, P, points):
        # homogeneous points (add 1's  to 3rd row)
        homogeneous_points = np.hstack([points, np.ones((points.shape[0], 1))])
        trans_homogeneous = P.dot(homogeneous_points.T).T
        #normlize back to 2D  (divide by last row)
        transformed_points = trans_homogeneous[:, :2] / trans_homogeneous[:, [2]]

        return transformed_points

    def calc_required_rotation(self):
        v = self.vertices[1] - self.vertices[-2]
        x, y = v
        return np.pi - np.arctan2(y, x)
