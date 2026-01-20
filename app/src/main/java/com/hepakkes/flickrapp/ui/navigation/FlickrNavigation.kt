package com.hepakkes.flickrapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hepakkes.flickrapp.ui.screens.PhotoDetailScreen
import com.hepakkes.flickrapp.ui.screens.PhotoGridScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val PHOTO_GRID = "photo_grid"
    const val PHOTO_DETAIL = "photo_detail/{photoId}/{photoSecret}/{photoServer}/{photoTitle}"

    fun photoDetail(photoId: String, photoSecret: String, photoServer: String, photoTitle: String): String {
        val encodedTitle = URLEncoder.encode(photoTitle.ifEmpty { "Photo" }, StandardCharsets.UTF_8.toString())
        return "photo_detail/$photoId/$photoSecret/$photoServer/$encodedTitle"
    }
}

@Composable
fun FlickrNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PHOTO_GRID
    ) {
        composable(Routes.PHOTO_GRID) {
            PhotoGridScreen(
                onPhotoClick = { photo ->
                    navController.navigate(
                        Routes.photoDetail(
                            photoId = photo.id,
                            photoSecret = photo.secret,
                            photoServer = photo.server,
                            photoTitle = photo.title
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.PHOTO_DETAIL,
            arguments = listOf(
                navArgument("photoId") { type = NavType.StringType },
                navArgument("photoSecret") { type = NavType.StringType },
                navArgument("photoServer") { type = NavType.StringType },
                navArgument("photoTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            val photoSecret = backStackEntry.arguments?.getString("photoSecret") ?: ""
            val photoServer = backStackEntry.arguments?.getString("photoServer") ?: ""
            val photoTitle = URLDecoder.decode(
                backStackEntry.arguments?.getString("photoTitle") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            PhotoDetailScreen(
                photoId = photoId,
                photoSecret = photoSecret,
                photoServer = photoServer,
                photoTitle = photoTitle,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
