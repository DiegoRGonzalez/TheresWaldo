
import java.util.Comparator;

public class SubimageComparator implements Comparator<Subimage>
{
    @Override
	public int compare(Subimage x, Subimage y)
	{
	    if (x.getCombConfLevel() < y.getCombConfLevel())
		{
		    return -1;
		}
	    if (x.getCombConfLevel() > y.getCombConfLevel())
		{
			return 1;
		}
	    return 0;
	}
}