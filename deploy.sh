set -e


rsync -a --delete --copy-links $(cat byransha-classpath.lst) bin/

echo "rsync to dronic"
rsync --progress -a --delete --delete-excluded --exclude-from deploy.exclude.lst ./ byransha@dronic.i3s.unice.fr:backend/
echo "restarting service"
echo response from server : $(curl -k 'https://dronic.i3s.unice.fr:8080/api?endpoint=kill')

exit


mkdir -p /tmp/byransha/bin/
echo "coping to tmp"
rsync -a --delete --copy-links $(cat byransha-classpath.lst) /tmp/byransha/bin/




echo Releasing to I3S website
N=byransha-$(date | tr ' ' '_').tgz
echo creating "/tmp/$N" 
tar czf "/tmp/$N" /tmp/byransha/bin/
echo "scp..."
scp /tmp/"$N" hogie@bastion.i3s.unice.fr:public_html/software/
echo last version is there: "https://webusers.i3s.unice.fr/~hogie/software/$N"
