# A Custom RecyclerView GridLayoutManager
LayoutManager should populate the items in the following way:

Item1 Item2 Item3 Item4 Item5  | Item11 Item12 Item13 Item14Item15
Item6 Item7 Item8 Item9 Item10 | Item16 Item17 ...

Project Structure
- UI: classic views framework which used in MainActivity
- Test data: DataProvider + DataItem value class
- List: RecyclerView, ItemsAdapter, CustomGridLayoutManager

CustomGridLayoutManager
The required list items order is reached by implementation of CustomGridLayoutManager. It contains full logic to construct, dispalay and scrool the views.

A main class property is **anchorPosition**. It is a position of a first top left .visible view. Tha value of  **anchorPosition** can be changed by horizontal scrolling.

The main functions for displaying the grid is **fill** and **fillVisibleGrid**.

#### fill(recycler: Recycler)
The method which collects all existing Views and detach them from RecyclerView. Then  **fillVisibleGrid** called which attach necessary view or creates new one and it ro RecyclerView. At the end not used views are removed from the list

#### fillVisibleGrid(recycler: Recycler, viewCache: SparseArray<View>)
Starting from the **anchorePosition** we need to decide if the detached view can be attached again or we need to create a new view and add it to the list. If the view is concidered not to be on the screen then it stays in the cache which will be cleared after.
