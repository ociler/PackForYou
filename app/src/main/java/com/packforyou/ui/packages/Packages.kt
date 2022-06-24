package com.packforyou.ui.packages;

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.packforyou.R
import com.packforyou.data.models.Package

@Composable
fun Packages(
    packagesViewModel: IPackagesViewModel
) {
    val packages = listOf<Package>()
    LazyColumn(
        modifier = Modifier.background(Color.Transparent)
    ) {
        item{
            Divider(
                thickness = 5.dp,
                color = Color.Black,
                modifier = Modifier
                    .padding(
                        horizontal = 80.dp,
                        vertical = 20.dp
                    )
                    .clip(RoundedCornerShape(50.dp))
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(Color.Transparent)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = "Logs out from de session"
                )
            }
        }

        items(packages) { packge ->


        }

        item {
            Box(Modifier.height(200.dp))
        }
    }


}
