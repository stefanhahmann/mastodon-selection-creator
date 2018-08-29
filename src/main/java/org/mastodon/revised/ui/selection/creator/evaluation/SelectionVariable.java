package org.mastodon.revised.ui.selection.creator.evaluation;

import java.util.BitSet;
import java.util.Iterator;

import org.mastodon.RefPool;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.model.SelectionModel;
import org.mastodon.revised.ui.selection.creator.evaluation.util.BitSetIterator;

/**
 * @author Jean-Yves Tinevez
 */
public class SelectionVariable
{

	final BitSet selectedVertices;

	final BitSet selectedEdges;

	/**
	 * Empty selection variable.
	 */
	SelectionVariable()
	{
		this( new BitSet(), new BitSet() );
	}

	SelectionVariable( final BitSet selectedVertices, final BitSet selectedEdges )
	{
		this.selectedVertices = selectedVertices;
		this.selectedEdges = selectedEdges;
	}

	/**
	 * Creates a {@link SelectionVariable} from a {@link SelectionModel}.
	 *
	 * @param selectionModel
	 *            the selection model.
	 * @param idmap
	 *            the mapping from graph objects to their id.
	 * @return a new {@link SelectionVariable}.
	 */
	public static < V extends Vertex< E >, E extends Edge< V > > SelectionVariable fromSelectionModel( final SelectionModel< V, E > selectionModel, final GraphIdBimap< V, E > idmap )
	{
		// Vertices.
		final BitSet sv = new BitSet();
		for ( final V v : selectionModel.getSelectedVertices() )
			sv.set( idmap.getVertexId( v ) );

		// Edges.
		final BitSet se = new BitSet();
		for ( final E e : selectionModel.getSelectedEdges() )
			se.set( idmap.getEdgeId( e ) );

		return new SelectionVariable( sv, se );
	}

	/**
	 * Creates a {@link SelectionVariable} containing all the vertices and all
	 * the edges of the specified graph.
	 *
	 * @param graph
	 *            the graph.
	 * @param idmap
	 *            the mapping from graph objects to their id.
	 * @return a new {@link SelectionVariable}.
	 */
	public static < V extends Vertex< E >, E extends Edge< V > > SelectionVariable fromGraph( final ReadOnlyGraph< V, E > graph, final GraphIdBimap< V, E > idmap )
	{
		final BitSet vs = new BitSet();
		final BitSet es = new BitSet();
		for ( final V v : graph.vertices() )
			vs.set( idmap.getVertexId( v ) );
		for ( final E e : graph.edges() )
			es.set( idmap.getEdgeId( e ) );
		return new SelectionVariable( vs, es );
	}

	public SelectionVariable inPlaceAdd( final SelectionVariable sv )
	{
		selectedVertices.or( sv.selectedVertices );
		selectedEdges.or( sv.selectedEdges );
		return this;
	}

	public SelectionVariable inPlaceSub( final SelectionVariable sv )
	{
		selectedVertices.andNot( sv.selectedVertices );
		selectedEdges.andNot( sv.selectedEdges );
		return this;
	}

	public SelectionVariable inPlaceAnd( final SelectionVariable sv )
	{
		selectedVertices.and( sv.selectedVertices );
		selectedEdges.and( sv.selectedEdges );
		return this;
	}

	@Override
	public String toString()
	{
		return "Selection( " + selectedVertices.cardinality() + ", " + selectedEdges.cardinality() + " )";
	}

	public SelectionVariable copy()
	{
		return new SelectionVariable( ( BitSet ) selectedVertices.clone(), ( BitSet ) selectedEdges.clone() );
	}

	public void clearVertices()
	{
		selectedVertices.clear();
	}

	public void clearEdges()
	{
		selectedEdges.clear();
	}

	/**
	 * Returns an iterator that will iterate over the vertices that are set in
	 * this {@link SelectionVariable}.
	 *
	 * @param pool
	 *            the vertex pool in which to take objects.
	 * @return a new iterator.
	 */
	public < V > Iterator< V > vertexIterator( final RefPool< V > pool )
	{
		final V ref = pool.createRef();
		final BitSetIterator it = new BitSetIterator( selectedVertices );
		return new Iterator< V >()
		{

			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public V next()
			{
				return pool.getObject( it.next(), ref );
			}
		};
	}

	/**
	 * Returns an iterator that will iterate over the edges that are set in this
	 * {@link SelectionVariable}.
	 *
	 * @param pool
	 *            the edge pool in which to take objects.
	 * @return a new iterator.
	 */
	public < E > Iterator< E > edgeIterator( final RefPool< E > pool )
	{
		final E ref = pool.createRef();
		final BitSetIterator it = new BitSetIterator( selectedEdges );
		return new Iterator< E >()
		{

			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public E next()
			{
				return pool.getObject( it.next(), ref );
			}
		};
	}
}
