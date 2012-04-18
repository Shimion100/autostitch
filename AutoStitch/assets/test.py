import cv


# load two our dataset images to test with
img1 = cv.LoadImageM("100_2991.JPG", cv.CV_LOAD_IMAGE_GRAYSCALE)
img2 = cv.LoadImageM("100_2992.JPG", cv.CV_LOAD_IMAGE_GRAYSCALE)

# Using cvExtractSURF
# find some keypoints using SURF on our two files
(img1_keypoints, img1_descriptors) = cv.ExtractSURF(img1, None, cv.CreateMemStorage(), (0, 30000, 3, 1))
(img2_keypoints, img2_descriptors) = cv.ExtractSURF(img2, None, cv.CreateMemStorage(), (0, 30000, 3, 1))

# Matching Key Points in Both Images

#do some KNN siliness


# Finding the Homography

cv.FindHomography(srcPoints, dstPoints, H, method, ransacReprojThreshold=3.0, status=None)
