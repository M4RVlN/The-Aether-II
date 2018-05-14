package com.gildedgames.aether.common.world.aether.island.nodename.as3delaunay;

/*
 * Java implementaition by Connor Clark (www.hotengames.com). Pretty much a 1:1
 * translation of a wonderful map generating algorthim by Amit Patel of Red Blob Games,
 * which can be found here (http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/)
 * Hopefully it's of use to someone out there who needed it in Java like I did!
 * Note, the only island mode implemented is Radial. Implementing more is generateDecorations for another day.
 *
 * FORTUNE'S ALGORTIHIM
 *
 * This is a java implementation of an AS3 (Flash) implementation of an algorthim
 * originally created in C++. Pretty much a 1:1 translation from as3 to java, save
 * for some necessary workarounds. Original as3 implementation by Alan Shaw (of nodename)
 * can be found here (https://github.com/nodename/as3delaunay). Original algorthim
 * by Steven Fortune (see lisence for c++ implementation below)
 *
 * The author of this software is Steven Fortune.  Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public final class Voronoi
{

	private SiteList _sites;

	private HashMap<Point, Site> _sitesIndexedByLocation;

	private ArrayList<Triangle> _triangles;

	private ArrayList<VoronoiEdge> _edges;

	// TODO generalize this so it doesn't have to be a rectangle;
	// then we can make the fractal voronois-within-voronois
	private Rectangle _plotBounds;

	public Voronoi(final ArrayList<Point> points, final ArrayList<Color> colors, final Rectangle plotBounds)
	{
		this.init(points, colors, plotBounds);
		this.fortunesAlgorithm();
	}

	//TODO check points for negative values. We must search min value too and translate whole list to fit it in positive quadrant.
	public Voronoi(final ArrayList<Point> points, final ArrayList<Color> colors)
	{
		double maxWidth = 0, maxHeight = 0;
		for (final Point p : points)
		{
			maxWidth = Math.max(maxWidth, p.x);
			maxHeight = Math.max(maxHeight, p.y);
		}
		System.out.println(maxWidth + "," + maxHeight);
		this.init(points, colors, new Rectangle(0, 0, maxWidth, maxHeight));
		this.fortunesAlgorithm();
	}

	public Voronoi(final int numSites, final Random r, final Rectangle _plotBounds)
	{
		final ArrayList<Point> points = new ArrayList();
		for (int i = 0; i < numSites; i++)
		{
			points.add(new Point(_plotBounds.x + (r.nextDouble() * _plotBounds.width), _plotBounds.y + (r.nextDouble() * _plotBounds.height)));
		}
		this.init(points, null, _plotBounds);
		this.fortunesAlgorithm();
	}

	public Voronoi(final int numSites, final double maxWidth, final double maxHeight, final Random r, final ArrayList<Color> colors)
	{
		final ArrayList<Point> points = new ArrayList();
		for (int i = 0; i < numSites; i++)
		{
			points.add(new Point(r.nextDouble() * maxWidth, r.nextDouble() * maxHeight));
		}
		this.init(points, colors, new Rectangle(0, 0, maxWidth, maxHeight));
		this.fortunesAlgorithm();
	}

	public static int compareByYThenX(final Site s1, final Site s2)
	{
		if (s1.get_y() < s2.get_y())
		{
			return -1;
		}
		if (s1.get_y() > s2.get_y())
		{
			return 1;
		}
		return Double.compare(s1.get_x(), s2.get_x());
	}

	public static int compareByYThenX(final Site s1, final Point s2)
	{
		if (s1.get_y() < s2.y)
		{
			return -1;
		}
		if (s1.get_y() > s2.y)
		{
			return 1;
		}
		return Double.compare(s1.get_x(), s2.x);
	}

	public Rectangle get_plotBounds()
	{
		return this._plotBounds;
	}

	public void dispose()
	{
		int i, n;
		if (this._sites != null)
		{
			this._sites.dispose();
			this._sites = null;
		}
		if (this._triangles != null)
		{
			n = this._triangles.size();
			for (i = 0; i < n; ++i)
			{
				this._triangles.get(i).dispose();
			}
			this._triangles.clear();
			this._triangles = null;
		}
		if (this._edges != null)
		{
			n = this._edges.size();
			for (i = 0; i < n; ++i)
			{
				this._edges.get(i).dispose();
			}
			this._edges.clear();
			this._edges = null;
		}
		this._plotBounds = null;
		this._sitesIndexedByLocation = null;
	}

	private void init(final ArrayList<Point> points, final ArrayList<Color> colors, final Rectangle plotBounds)
	{
		this._sites = new SiteList();
		this._sitesIndexedByLocation = new HashMap();
		this.addSites(points, colors);
		this._plotBounds = plotBounds;
		this._triangles = new ArrayList();
		this._edges = new ArrayList();
	}

	private void addSites(final ArrayList<Point> points, final ArrayList<Color> colors)
	{
		final int length = points.size();
		for (int i = 0; i < length; ++i)
		{
			this.addSite(points.get(i), colors != null ? colors.get(i) : null, i);
		}
	}

	private void addSite(final Point p, final Color color, final int index)
	{
		final double weight = Math.random() * 100;
		final Site site = Site.create(p, index, weight, color);
		this._sites.push(site);
		this._sitesIndexedByLocation.put(p, site);
	}

	public ArrayList<VoronoiEdge> edges()
	{
		return this._edges;
	}

	public ArrayList<Point> region(final Point p)
	{
		final Site site = this._sitesIndexedByLocation.get(p);
		if (site == null)
		{
			return new ArrayList();
		}
		return site.region(this._plotBounds);
	}

	// TODO: bug: if you call this before you call region(), generateDecorations goes wrong :(
	public ArrayList<Point> neighborSitesForSite(final Point coord)
	{
		final ArrayList<Point> points = new ArrayList();
		final Site site = this._sitesIndexedByLocation.get(coord);
		if (site == null)
		{
			return points;
		}
		final ArrayList<Site> sites = site.neighborSites();
		for (final Site neighbor : sites)
		{
			points.add(neighbor.get_coord());
		}
		return points;
	}

	public ArrayList<Circle> circles()
	{
		return this._sites.circles();
	}

	private ArrayList<VoronoiEdge> selectEdgesForSitePoint(final Point coord, final ArrayList<VoronoiEdge> edgesToTest)
	{
		final ArrayList<VoronoiEdge> filtered = new ArrayList();

		for (final VoronoiEdge e : edgesToTest)
		{
			if (((e.get_leftSite() != null && e.get_leftSite().get_coord() == coord)
					|| (e.get_rightSite() != null && e.get_rightSite().get_coord() == coord)))
			{
				filtered.add(e);
			}
		}
		return filtered;

        /*function myTest(edge:VoronoiEdge, index:int, vector:Vector.<VoronoiEdge>):Boolean
		 {
         return ((edge.leftSite && edge.leftSite.coord == coord)
         ||  (edge.rightSite && edge.rightSite.coord == coord));
         }*/
	}

	private ArrayList<LineSegment> visibleLineSegments(final ArrayList<VoronoiEdge> edges)
	{
		final ArrayList<LineSegment> segments = new ArrayList();

		for (final VoronoiEdge edge : edges)
		{
			if (edge.get_visible())
			{
				final Point p1 = edge.get_clippedEnds().get(LR.LEFT);
				final Point p2 = edge.get_clippedEnds().get(LR.RIGHT);
				segments.add(new LineSegment(p1, p2));
			}
		}

		return segments;
	}

	private ArrayList<LineSegment> delaunayLinesForEdges(final ArrayList<VoronoiEdge> edges)
	{
		final ArrayList<LineSegment> segments = new ArrayList();

		for (final VoronoiEdge edge : edges)
		{
			segments.add(edge.delaunayLine());
		}

		return segments;
	}

	public ArrayList<LineSegment> voronoiBoundaryForSite(final Point coord)
	{
		return this.visibleLineSegments(this.selectEdgesForSitePoint(coord, this._edges));
	}

	public ArrayList<LineSegment> delaunayLinesForSite(final Point coord)
	{
		return this.delaunayLinesForEdges(this.selectEdgesForSitePoint(coord, this._edges));
	}

	public ArrayList<LineSegment> voronoiDiagram()
	{
		return this.visibleLineSegments(this._edges);
	}

	/*public ArrayList<LineSegment> delaunayTriangulation(keepOutMask:BitmapData = null)
	 {
	 return delaunayLinesForEdges(selectNonIntersectingEdges(keepOutMask, _edges));
	 }*/
	public ArrayList<LineSegment> hull()
	{
		return this.delaunayLinesForEdges(this.hullEdges());
	}

	private ArrayList<VoronoiEdge> hullEdges()
	{
		final ArrayList<VoronoiEdge> filtered = new ArrayList();

		for (final VoronoiEdge e : this._edges)
		{
			if (e.isPartOfConvexHull())
			{
				filtered.add(e);
			}
		}

		return filtered;

        /*function myTest(edge:VoronoiEdge, index:int, vector:Vector.<VoronoiEdge>):Boolean
		 {
         return (edge.isPartOfConvexHull());
         }*/
	}

    /*public ArrayList<Integer> siteColors(referenceImage:BitmapData = null)
	 {
     return _sites.siteColors(referenceImage);
     }*/

	public ArrayList<Point> hullPointsInOrder()
	{
		ArrayList<VoronoiEdge> hullEdges = this.hullEdges();

		final ArrayList<Point> points = new ArrayList();
		if (hullEdges.isEmpty())
		{
			return points;
		}

		final EdgeReorderer reorderer = new EdgeReorderer(hullEdges, Site.class);
		hullEdges = reorderer.get_edges();
		final ArrayList<LR> orientations = reorderer.get_edgeOrientations();
		reorderer.dispose();

		LR orientation;

		final int n = hullEdges.size();
		for (int i = 0; i < n; ++i)
		{
			final VoronoiEdge edge = hullEdges.get(i);
			orientation = orientations.get(i);
			points.add(edge.site(orientation).get_coord());
		}
		return points;
	}

	/*public ArrayList<LineSegment> spanningTree(String type, keepOutMask:BitmapData = null)
	 {
	 ArrayList<VoronoiEdge>  edges = selectNonIntersectingEdges(keepOutMask, _edges);
	 ArrayList<LineSegment>  segments = delaunayLinesForEdges(edges);
	 return kruskal(segments, type);
	 }*/
	public ArrayList<ArrayList<Point>> regions()
	{
		return this._sites.regions(this._plotBounds);
	}

	public ArrayList<Point> siteCoords()
	{
		return this._sites.siteCoords();
	}

	private void fortunesAlgorithm()
	{
		Site newSite, bottomSite, topSite, tempSite;
		Vertex v, vertex;
		Point newintstar = null;
		LR leftRight;
		Halfedge lbnd, rbnd, llbnd, rrbnd, bisector;
		VoronoiEdge edge;

		final Rectangle dataBounds = this._sites.getSitesBounds();

		final int sqrt_nsites = (int) Math.sqrt(this._sites.get_length() + 4);
		final HalfedgePriorityQueue heap = new HalfedgePriorityQueue(dataBounds.y, dataBounds.height, sqrt_nsites);
		final EdgeList edgeList = new EdgeList(dataBounds.x, dataBounds.width, sqrt_nsites);
		final ArrayList<Halfedge> halfEdges = new ArrayList();
		final ArrayList<Vertex> vertices = new ArrayList();

		final Site bottomMostSite = this._sites.next();
		newSite = this._sites.next();

		for (; ; )
		{
			if (heap.empty() == false)
			{
				newintstar = heap.min();
			}

			if (newSite != null
					&& (heap.empty() || compareByYThenX(newSite, newintstar) < 0))
			{
				/* new site is smallest */
				//trace("smallest: new site " + newSite);

				// Step 8:
				lbnd = edgeList.edgeListLeftNeighbor(newSite.get_coord());    // the Halfedge just to the left of newSite
				//trace("lbnd: " + lbnd);
				rbnd = lbnd.edgeListRightNeighbor;        // the Halfedge just to the right
				//trace("rbnd: " + rbnd);
				bottomSite = this.rightRegion(lbnd, bottomMostSite);        // this is the same as leftRegion(rbnd)
				// this Site determines the region containing the new site
				//trace("new Site is in region of existing site: " + bottomSite);

				// Step 9:
				edge = VoronoiEdge.createBisectingEdge(bottomSite, newSite);
				//trace("new edge: " + edge);
				this._edges.add(edge);

				bisector = Halfedge.create(edge, LR.LEFT);
				halfEdges.add(bisector);
				// inserting two Halfedges into edgeList constitutes Step 10:
				// insert bisector to the right of lbnd:
				edgeList.insert(lbnd, bisector);

				// first half of Step 11:
				if ((vertex = Vertex.intersect(lbnd, bisector)) != null)
				{
					vertices.add(vertex);
					heap.remove(lbnd);
					lbnd.vertex = vertex;
					lbnd.ystar = vertex.get_y() + newSite.dist(vertex);
					heap.insert(lbnd);
				}

				lbnd = bisector;
				bisector = Halfedge.create(edge, LR.RIGHT);
				halfEdges.add(bisector);
				// second Halfedge for Step 10:
				// insert bisector to the right of lbnd:
				edgeList.insert(lbnd, bisector);

				// second half of Step 11:
				if ((vertex = Vertex.intersect(bisector, rbnd)) != null)
				{
					vertices.add(vertex);
					bisector.vertex = vertex;
					bisector.ystar = vertex.get_y() + newSite.dist(vertex);
					heap.insert(bisector);
				}

				newSite = this._sites.next();
			}
			else if (heap.empty() == false)
			{
				/* intersection is smallest */
				lbnd = heap.extractMin();
				llbnd = lbnd.edgeListLeftNeighbor;
				rbnd = lbnd.edgeListRightNeighbor;
				rrbnd = rbnd.edgeListRightNeighbor;
				bottomSite = this.leftRegion(lbnd, bottomMostSite);
				topSite = this.rightRegion(rbnd, bottomMostSite);
				// these three sites define a Delaunay triangle
				// (not actually using these for anything...)
				//_triangles.push(new Triangle(bottomSite, topSite, rightRegion(lbnd)));

				v = lbnd.vertex;
				v.setIndex();
				lbnd.edge.setVertex(lbnd.leftRight, v);
				rbnd.edge.setVertex(rbnd.leftRight, v);
				edgeList.remove(lbnd);
				heap.remove(rbnd);
				edgeList.remove(rbnd);
				leftRight = LR.LEFT;
				if (bottomSite.get_y() > topSite.get_y())
				{
					tempSite = bottomSite;
					bottomSite = topSite;
					topSite = tempSite;
					leftRight = LR.RIGHT;
				}
				edge = VoronoiEdge.createBisectingEdge(bottomSite, topSite);
				this._edges.add(edge);
				bisector = Halfedge.create(edge, leftRight);
				halfEdges.add(bisector);
				edgeList.insert(llbnd, bisector);
				edge.setVertex(LR.other(leftRight), v);
				if ((vertex = Vertex.intersect(llbnd, bisector)) != null)
				{
					vertices.add(vertex);
					heap.remove(llbnd);
					llbnd.vertex = vertex;
					llbnd.ystar = vertex.get_y() + bottomSite.dist(vertex);
					heap.insert(llbnd);
				}
				if ((vertex = Vertex.intersect(bisector, rrbnd)) != null)
				{
					vertices.add(vertex);
					bisector.vertex = vertex;
					bisector.ystar = vertex.get_y() + bottomSite.dist(vertex);
					heap.insert(bisector);
				}
			}
			else
			{
				break;
			}
		}

		// heap should be empty now
		heap.dispose();
		edgeList.dispose();

		for (final Halfedge halfEdge : halfEdges)
		{
			halfEdge.reallyDispose();
		}
		halfEdges.clear();

		// we need the vertices to clip the edges
		for (final VoronoiEdge e : this._edges)
		{
			e.clipVertices(this._plotBounds);
		}
		// but we don't actually ever use them again!
		for (final Vertex v0 : vertices)
		{
			v0.dispose();
		}
		vertices.clear();

	}

	Site leftRegion(final Halfedge he, final Site bottomMostSite)
	{
		final VoronoiEdge edge = he.edge;
		if (edge == null)
		{
			return bottomMostSite;
		}
		return edge.site(he.leftRight);
	}

	Site rightRegion(final Halfedge he, final Site bottomMostSite)
	{
		final VoronoiEdge edge = he.edge;
		if (edge == null)
		{
			return bottomMostSite;
		}
		return edge.site(LR.other(he.leftRight));
	}
}
