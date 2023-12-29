echo "shutting down $(hostname)"
kill -9 $(ps aux | grep -e "^$USER" | grep java | head -n-1 | awk '{print $2}')
